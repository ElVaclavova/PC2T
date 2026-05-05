package zamestnancidb.model;

public enum CollaborationQuality {
	ZLA ("zla", 1),
	PRIEMERNA ("priemerna", 2),
	DOBRA("dobra", 3);
	
	private final String label;
	private final int value;
	
	CollaborationQuality(String label, int value) {
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	public int getValue() {
		return value;
	}
	
	public static CollaborationQuality fromValue(int value) {
		for (CollaborationQuality q : values()) {
			if (q.value == value) {
				return q;
			}
		}
		throw new IllegalArgumentException("Neznama hodnota: " + value);
	}
	
	@Override
	public String toString() {
		return label;
	}

}
