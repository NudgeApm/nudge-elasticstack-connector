package org.nudge.elasticstack.mapper;

/**
 * @author tarnaud
 *
 * @param <N> type of Nudge API pojo
 */
public interface Mapper<N> {
	
	/**
	 * convert a Nudge API pojo to csv log parseable by logstash
	 * @param n
	 * @return
	 */
	String convertForLogstash(N n);
	
}
