package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;

public class Event extends AbstractEvaluator<Object> {
	private static final Logger s_logger = LoggerFactory.getLogger(Event.class);
	private static final Parameters PARAMETERS;
	private static final Function BOOLEANFUNC = new Function("#BOOL$", 1);
	private static final Function STRINGFUNC = new Function("#STR$", 1);
	private static final Function SHORTFUNC = new Function("#SHORT$", 1);
	private static final Function INTEGERFUNC = new Function("#INT$", 1);
	private static final Function CHANNELFUNC = new Function("#CHANNEL$", 1);
	private static final Operator ASSIGN = new Operator("=", 2, Operator.Associativity.LEFT, 5);
	private static final Operator LESSEREQ = new Operator("<=", 1, Operator.Associativity.LEFT, 4);
	private static final Operator LARGEREQ = new Operator(">=", 1, Operator.Associativity.LEFT, 4);
	private static final Operator LESSER = new Operator("<", 1, Operator.Associativity.LEFT, 4);
	private static final Operator LARGER = new Operator(">", 1, Operator.Associativity.LEFT, 4);
	private static final Operator NEQUAL = new Operator("!=", 1, Operator.Associativity.LEFT, 3);
	private static final Operator EQUAL = new Operator("==", 1, Operator.Associativity.LEFT, 3);
	private static final Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
	private static final Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 1);

	static {
		PARAMETERS = new Parameters();
		PARAMETERS.add(AND);
		PARAMETERS.add(OR);
		PARAMETERS.add(EQUAL);
		PARAMETERS.add(NEQUAL);
		PARAMETERS.add(LARGER);
		PARAMETERS.add(LESSER);
		PARAMETERS.add(LARGEREQ);
		PARAMETERS.add(LESSEREQ);
		PARAMETERS.add(BOOLEANFUNC);
		PARAMETERS.add(STRINGFUNC);
		PARAMETERS.add(SHORTFUNC);
		PARAMETERS.add(INTEGERFUNC);
		PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
	}

	private static Map<String, TopicChannel<?>> ParseEvent(String event) {
		List<String> channelIds = new ArrayList<>();
		int idx = event.indexOf("#CHANNEL$(");
		int idx2 = event.indexOf(")");

		while (idx != -1 && idx2 != -1) {
			channelIds.add(event.substring(idx + 10, idx2 - 1));
			try {
				event = event.substring(idx2 + 1);
			} catch (IndexOutOfBoundsException e) {
				break;
			}

			idx = event.indexOf("#CHANNEL$(");
			idx2 = event.indexOf(")");
		}

		return EventManager.getInstance().getTopicChannels(channelIds);
	}

	private String eventId;
	private String eventString;
	private String ifString;
	private String thenString;
	private Map<String, TopicChannel<?>> ifChannels;
	private Map<String, TopicChannel<?>> thenChannels;

	public Event(String ifString, String thenString) {
		super(PARAMETERS);
		this.ifString = ifString;
		this.thenString = thenString;
		this.eventString = "If\n" + ifString + "\nThen\n" + thenString;
		s_logger.debug("New event rule: " + eventString);

		ifChannels = ParseEvent(ifString);
		thenChannels = ParseEvent(thenString);
	}

	public boolean evaluate() {
		String eva = new String(ifString);
		Set<Entry<String, TopicChannel<?>>> entries = ifChannels.entrySet();
		for (Entry<String, TopicChannel<?>> entry : entries) {
			eva.replace(entry.getKey(), entry.getValue().getValue().toString());
		}

		return (Boolean) evaluate(eva);
	}

	public boolean execute() {
		return (Boolean) evaluate(thenString);
	}

	@Override
	protected Object evaluate(Function function, Iterator<Object> arguments, Object evaluationContext) {
		if (function == BOOLEANFUNC) {
			return Boolean.valueOf(arguments.next().toString());
		} else if (function == STRINGFUNC) {
			return arguments.next().toString();
		} else if (function == SHORTFUNC) {
			return Short.valueOf(arguments.next().toString());
		} else if (function == INTEGERFUNC) {
			return Integer.valueOf(arguments.next().toString());
		} else if (function == CHANNELFUNC) {
			return thenChannels.get(arguments.toString());
		}

		return super.evaluate(function, arguments, evaluationContext);
	}

	@Override
	protected Object evaluate(Operator operator, Iterator<Object> operands, Object evaluationContext) {
		if (operator == EQUAL) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof String) {
				return Boolean.valueOf(((String) o1).compareToIgnoreCase((String) o2) == 0);
			} else if (o1 instanceof Short) {
				if (o2 instanceof Short)
					return ((Short) o1 == (Short) o2);
				else if (o2 instanceof Integer)
					return (((Short) o1).intValue() == (Integer) o2);
			} else if (o1 instanceof Integer) {
				if (o2 instanceof Integer)
					return ((Integer) o1 == (Integer) o2);
				else if (o2 instanceof Short)
					return ((Integer) o1 == ((Short) o2).intValue());
			} else if (o1 instanceof Boolean) {
				return Boolean.valueOf((((Boolean) o1).compareTo((Boolean) o2) == 0));
			}
		} else if (operator == NEQUAL) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof String) {
				return Boolean.valueOf(((String) o1).compareToIgnoreCase((String) o2) != 0);
			} else if (o1 instanceof Short) {
				if (o2 instanceof Short)
					return ((Short) o1 != (Short) o2);
				else if (o2 instanceof Integer)
					return (((Short) o1).intValue() != (Integer) o2);
			} else if (o1 instanceof Integer) {
				if (o2 instanceof Integer)
					return ((Integer) o1 != (Integer) o2);
				else if (o2 instanceof Short)
					return ((Integer) o1 != ((Short) o2).intValue());
			} else if (o1 instanceof Boolean) {
				return Boolean.valueOf((((Boolean) o1).compareTo((Boolean) o2) != 0));
			}
		} else if (operator == OR) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof Boolean && o2 instanceof Boolean) {
				return (Boolean) o1 || (Boolean) o2;
			}
		} else if (operator == AND) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof Boolean && o2 instanceof Boolean) {
				return (Boolean) o1 && (Boolean) o2;
			}
		} else if (operator == LARGER) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof Short) {
				if (o2 instanceof Short)
					return ((Short) o1 > (Short) o2);
				else if (o2 instanceof Integer)
					return ((Short) o1 > (Integer) o2);
			} else if (o1 instanceof Integer) {
				if (o2 instanceof Integer)
					return ((Integer) o1 > (Integer) o2);
				else if (o2 instanceof Short)
					return ((Integer) o1 > (Short) o2);
			}
		} else if (operator == LARGEREQ) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof Short) {
				if (o2 instanceof Short)
					return ((Short) o1 >= (Short) o2);
				else if (o2 instanceof Integer)
					return ((Short) o1 >= (Integer) o2);
			} else if (o1 instanceof Integer) {
				if (o2 instanceof Integer)
					return ((Integer) o1 >= (Integer) o2);
				else if (o2 instanceof Short)
					return ((Integer) o1 >= (Short) o2);
			}
		} else if (operator == LESSER) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof Short) {
				if (o2 instanceof Short)
					return ((Short) o1 < (Short) o2);
				else if (o2 instanceof Integer)
					return ((Short) o1 < (Integer) o2);
			} else if (o1 instanceof Integer) {
				if (o2 instanceof Integer)
					return ((Integer) o1 < (Integer) o2);
				else if (o2 instanceof Short)
					return ((Integer) o1 < (Short) o2);
			}
		} else if (operator == LESSEREQ) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof Short) {
				if (o2 instanceof Short)
					return ((Short) o1 <= (Short) o2);
				else if (o2 instanceof Integer)
					return ((Short) o1 <= (Integer) o2);
			} else if (o1 instanceof Integer) {
				if (o2 instanceof Integer)
					return ((Integer) o1 <= (Integer) o2);
				else if (o2 instanceof Short)
					return ((Integer) o1 <= (Short) o2);
			}
		} else if (operator == ASSIGN) {
			Object o1 = operands.next();
			Object o2 = operands.next();
			if (o1 instanceof TopicChannel<?>) {
				if (o2 instanceof Short)
					return Boolean.valueOf(((TopicChannel<Short>) o1).setValue((Short) o2) == (Short) o2);
				else if (o2 instanceof Integer)
					return Boolean.valueOf(((TopicChannel<Integer>) o1).setValue((Integer) o2) == (Integer) o2);
				else if (o2 instanceof String)
					return Boolean.valueOf(((TopicChannel<String>) o1).setValue((String) o2) == (String) o2);
				else if (o2 instanceof Boolean)
					return Boolean.valueOf(((TopicChannel<Boolean>) o1).setValue((Boolean) o2) == (Boolean) o2);
			}
		}

		return super.evaluate(operator, operands, evaluationContext);
	}

	@Override
	protected Boolean toValue(String literal, Object evaluationContext) {
		return Boolean.valueOf(literal);
	}

	public String setEventId(String id) {
		this.eventId = id;
		return this.eventId;
	}

	public String getEventId() {
		return this.eventId;
	}

	public String getIfString() { return this.ifString; }
	public String getThenString() { return this.eventString; }
}
