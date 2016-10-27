package org.nudge.elasticstack.context.nudge.filter;

import com.nudge.apm.buffer.probe.RawDataProtocol;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.context.nudge.filter.bean.Filter;
import org.nudge.elasticstack.context.nudge.filter.bean.Scope;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterManager {

	private static final Logger LOG = Logger.getLogger(FilterManager.class);

	private static final Pattern reqHeaderPattern = Pattern.compile("\\{(REQ_HEADER:)(.*?)\\}");
	private static final Pattern headerPattern = Pattern.compile("\\{(RESP_HEADER:)(.*?)\\}");
	private static final Pattern paramPattern = Pattern.compile("\\{(REQ_PARAM:)(.*?)\\}");

	private FilterManager() {
	}

	/**
	 * Recherche le premier filtre qui correspond avec l'url et la transaction passés en argument.
	 *
	 * @param trans
	 *          la transaction courante relative à l'url
	 * @param url
	 *          l'url en cours
	 * @return le premier filtre qui matches avec l'url, null si aucun filtre ne correspond
	 */
	public static Filter findFilter(List<Filter> filters, final RawDataProtocol.Transaction trans, final String url) {
		// recherche du 1er filtre qui matche avec l'url en cours
		if (filters != null && !filters.isEmpty()) {
			for (Filter filter : filters) {
				if (!filter.getStatus().equals(Filter.Status.ACTIVE)) {
					continue;
				}
				boolean isFilterMatches = true;
				for (Scope filterScope : filter.getScopes()) {
					if (!filterScopeMatches(filterScope, trans, url)) {
						isFilterMatches = false;
						break;
					}
				}
				if (isFilterMatches) {
					return filter;
				}
			}
		}
		return null;
	}

	private static boolean filterScopeMatches(Scope filterScope, RawDataProtocol.Transaction curTrans, String code) {
		Scope.Type type = filterScope.getType();
		try {
			switch (type) {
				case IP:
					return curTrans.getUserIp().matches(filterScope.getRegex());
				case CODE:
					return code.matches(filterScope.getRegex());
				case METHOD:
					return curTrans.getReqMethod().toString().matches(filterScope.getValue());
				case USER_AGENT:
					return curTrans.getUserAgent().matches(filterScope.getRegex());
				case RESP_HEADER:
					List<RawDataProtocol.Param> params = curTrans.getHeadersList();
					for (RawDataProtocol.Param param : params) {
						if (filterScope.getKey() != null && param.getKey().equals(filterScope.getKey())) {
							return param.getValue().matches(filterScope.getRegex());
						}
					}
					return false;
				case REQ_HEADER:
					List<RawDataProtocol.Param> reqParams = curTrans.getReqHeadersList();
					for (RawDataProtocol.Param param : reqParams) {
						if (filterScope.getKey() != null && param.getKey().equals(filterScope.getKey())) {
							return param.getValue().matches(filterScope.getRegex());
						}
					}
					return false;
				default:
					return false;
			}
		} catch (PatternSyntaxException pse) {
			LOG.error("Regex failed on filter " + filterScope.getType() + "|" + filterScope.getOperator() + "|" + filterScope.getValue() + " : " + pse.getDescription() + " @" + pse.getIndex());
			return false;
		}
	}

	/**
	 * Construit l'URL cible de la transaction à partir d'un filtre.
	 * Attention : Avant d'utiliser cette méthode, il peut être bon de vérifier que le filter matche à la transaction.
	 *
	 * @param trans
	 *          la transaction en provenance du RawData
	 * @param url
	 *          l'url de la transaction qui a pu être limité en longeur
	 * @param filter
	 *          le filtre qui a été sélectionné pour cette transaction
	 * @return l'url du segment
	 * @throws IllegalArgumentException
	 *           exception lancée si au moins un des paramètres est null
	 */
	public static String constructTargetUrl(final RawDataProtocol.Transaction trans, final String url, Filter filter) {

		if (filter != null && trans != null && url != null) {
			// si l'url contient au moins un element {#x} ou x égal un nombre décimal
			String completedTargetUrl = filter.getTargetCode();
			if (filter.getTargetCode().matches(".*\\{(URL)\\}.*")) {
				completedTargetUrl = completedTargetUrl.replaceAll("\\{(URL)\\}", url);
			}
			if (filter.getTargetCode().matches(".*\\{(IP)\\}.*")) {
				completedTargetUrl = completedTargetUrl.replaceAll("\\{(IP)\\}", trans.getUserIp());
			}
			if (filter.getTargetCode().matches(".*\\{(METHOD)\\}.*")) {
				completedTargetUrl = completedTargetUrl.replaceAll("\\{(METHOD)\\}", trans.getReqMethod().toString());
			}
			if (filter.getTargetCode().matches(".*\\{(USER_AGENT)\\}.*")) {
				completedTargetUrl = completedTargetUrl.replaceAll("\\{(USER_AGENT)\\}", trans.getUserAgent());
			}
			if (filter.getTargetCode().matches(".*\\{(RESP_HEADER:).*\\}.*")) {
				Matcher m = headerPattern.matcher(filter.getTargetCode());
				while (m.find()) {
					String paramKey = m.group(2);
					boolean found = false;
					for (RawDataProtocol.Param headerParam : trans.getHeadersList()) {
						if (headerParam.getKey().equals(paramKey)) {
							completedTargetUrl = completedTargetUrl.replaceAll("\\{(RESP_HEADER:" + paramKey + ")\\}", headerParam.getValue());
							found = true;
							break;
						}
					}
					if(!found) {
						completedTargetUrl = completedTargetUrl.replaceAll("\\{(RESP_HEADER:" + paramKey + ")\\}", "");
					}
				}
			}
			if (filter.getTargetCode().matches(".*\\{(REQ_HEADER:).*\\}.*")) {
				Matcher m = reqHeaderPattern.matcher(filter.getTargetCode());
				while (m.find()) {
					String paramKey = m.group(2);
					boolean found = false;
					for (RawDataProtocol.Param headerParam : trans.getReqHeadersList()) {
						if (headerParam.getKey().equals(paramKey)) {
							completedTargetUrl = completedTargetUrl.replaceAll("\\{(REQ_HEADER:" + paramKey + ")\\}", headerParam.getValue());
							found = true;
							break;
						}
					}
					if(!found) {
						completedTargetUrl = completedTargetUrl.replaceAll("\\{(REQ_HEADER:" + paramKey + ")\\}", "");
					}
				}
			}
			if (filter.getTargetCode().matches(".*\\{(REQ_PARAM:).*\\}.*")) {
				Matcher m = paramPattern.matcher(filter.getTargetCode());
				while (m.find()) {
					String paramKey = m.group(2);
					boolean found = false;
					for (RawDataProtocol.Param headerParam : trans.getParamsList()) {
						if (headerParam.getKey().equals(paramKey)) {
							completedTargetUrl = completedTargetUrl.replaceAll("\\{(REQ_PARAM:" + paramKey + ")\\}", headerParam.getValue());
							found = true;
							break;
						}
					}
					if(!found) {
						completedTargetUrl = completedTargetUrl.replaceAll("\\{(REQ_PARAM:" + paramKey + ")\\}", "");
					}
				}
			}
			return completedTargetUrl;
		}
		throw new IllegalArgumentException("At least one parameter is null");
	}

}
