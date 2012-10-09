package fr.soat.bean;

import com.google.common.base.Objects;

/**
 * @author Mouhcine MOULOU
 *
 */
public class Product {

	private String ref; 
	
	private String name;
	
	private int quantity;
	
	private double unitPrice;
	
	public String getRef() {
		return ref;
	}
	
	public Product(String ref, String name, int quantity, double unitPrice) {
		super();
		this.ref = ref;
		this.name = name;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * Default constructor 
	 */
	public Product() {
		super();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + quantity;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		long temp;
		temp = Double.doubleToLongBits(unitPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Product other = (Product) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (quantity != other.quantity)
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		if (Double.doubleToLongBits(unitPrice) != Double
				.doubleToLongBits(other.unitPrice))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("ref: ", ref).
				add("name: ", name).
				add("quantity: ", quantity).
				add("unitPrice: ", unitPrice).
				toString();
	}
}
