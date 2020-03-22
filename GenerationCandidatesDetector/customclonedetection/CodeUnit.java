package similaritycalculation;

/**
 * Piece of code, whether it is a general file, a class, a method, etc.
 * Represented by its type, an identifier and its string content.
 */
public class CodeUnit {
	/** Unique identifier. */
	private String id;

	/** Simple identifier. */
	private String name;

	/** String content of the code unit. */
	private String content;

	/** Type of content that the code unit contains. */
	private CodeUnitType type;

	/**
	 * Creates a code unit.
	 * 
	 * @param id
	 *            Identifier string.
	 * @param content
	 *            String content of the code unit.
	 * @param type
	 *            Type of content that the code unit contains.
	 */
	public CodeUnit(String id, String name, String content, CodeUnitType type) {
		this.id = id;
		this.name = name;
		this.content = content;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public CodeUnitType getType() {
		return type;
	}

	public void setType(CodeUnitType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeUnit other = (CodeUnit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
