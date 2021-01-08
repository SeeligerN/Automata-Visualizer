package automata;

public class Operation {

	public static final int IGNORE_STACK = 0;
	public static final int NOP = 1;
	public static final int PUSH = 2;
	public static final int POP = 3;
	
	private int toNode;
	private String onStack;
	private int operation;
	private String pushToStack;
	
	private String tempInput;
	
	public Operation(int toNode) {
		this(toNode, "", IGNORE_STACK, "");
	}
	
	public Operation(int toNode, String onStack, int operation, String pushToStack) {
		this.toNode = toNode;
		this.onStack = onStack;
		this.operation = operation;
		this.pushToStack = pushToStack;
	}

	public int getToNode() {
		return toNode;
	}

	public void setToNode(int toNode) {
		this.toNode = toNode;
	}
	
	public String getOnStack() {
		return onStack;
	}
	
	public void setOnStack(String onStack) {
		this.onStack = onStack;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public String getPushToStack() {
		return pushToStack;
	}

	public void setPushToStack(String pushToStack) {
		this.pushToStack = pushToStack;
	}
	
	public String getInputReq() {
		return tempInput;
	}
	
	public void setInputReq(String input) {
		this.tempInput = input;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Operation))
			return false;
		Operation o = (Operation) obj;
		
		if (o.getOperation() != operation)
			return false;
		
		switch(operation) {
		case PUSH:
			if (!o.getPushToStack().equals(pushToStack))
				return false;
		case POP:
		case NOP:
			if (!o.getOnStack().equals(onStack))
				return false;
		case IGNORE_STACK:
			if (o.getToNode() != toNode)
				return false;
		}
		
		return true;
	}

	public boolean isValid() {
		if (operation < 0 || operation > 3)
			return false;
		if (toNode == -1)
			return false;
		
		return true;
	}
}
