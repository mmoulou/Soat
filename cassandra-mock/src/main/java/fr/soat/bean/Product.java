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
	public String toString() {
		return Objects.toStringHelper(this).
				add("ref: ", ref).
				add("name: ", name).
				add("quantity: ", quantity).
				add("unitPrice: ", unitPrice).
				toString();
	}
}
