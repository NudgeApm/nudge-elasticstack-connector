package org.nudge.elasticstack.context.nudge.api.bean;


import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Fred on 04/10/2016.
 */
public class Scope {

    public enum Type {
        CODE,
        METHOD,
        USER_AGENT,
        REQ_HEADER,
        RESP_HEADER,
        IP;

		@JsonCreator
		public static Type fromString(String key) {
			for(Type type : Type.values()) {
				if(type.name().equalsIgnoreCase(key)) {
					return type;
				}
			}
			return null;
		}

    }

    public static enum Operator {
        EQUALS,
        CONTAINS,
        START_WITH,
        END_WITH,
        GREATER_THAN,
        LOWER_THAN,
        DIFFERS,
        REGEX,
        WILDCARD;

		@JsonCreator
		public static Operator fromString(String key) {
			for(Operator operator : Operator.values()) {
				if(operator.name().equalsIgnoreCase(key)) {
					return operator;
				}
			}
			return null;
		}
    }

    private Type type;
    private String key;
    private Operator operator;
    private String value;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	/**
	 * Convertit la valeur d'un scope en regex à partir de son opérateur.
	 *
	 * @return la regex qui correspond au scope
	 */
	public String getRegex() {
		String regex = "";
		if (null != value && !value.trim().isEmpty()) {
			if (operator.equals(Operator.CONTAINS)) {
				regex = ".*" + espcapeRegexChars(value) + ".*";
			} else if (operator.equals(Operator.EQUALS)) {
				regex = espcapeRegexChars(value);
			} else if (operator.equals(Operator.DIFFERS)) {
				regex = "^ " + espcapeRegexChars(value);
			} else if (operator.equals(Operator.START_WITH)) {
				regex = espcapeRegexChars(value) + ".*";
			} else if (operator.equals(Operator.END_WITH)) {
				regex = ".*" + espcapeRegexChars(value);
			} else if (operator.equals(Operator.WILDCARD)) {
				regex = value.replaceAll("\\*", ".*").replaceAll("\\?", ".");
			} else if (operator.equals(Operator.REGEX)) {
				regex = value;
			}
		}
		return regex;
	}

	private static String espcapeRegexChars(String str) {
		return str.replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\\\*").replaceAll("\\[", "\\\\[");
	}

	@Override
    public String toString() {
        return "Scope{" +
                "type=" + type +
                ", key='" + key + '\'' +
                ", operator=" + operator +
                ", value='" + value + '\'' +
                '}';
    }
}
