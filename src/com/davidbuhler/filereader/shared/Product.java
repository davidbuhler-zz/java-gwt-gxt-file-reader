package com.davidbuhler.filereader.shared;

public class Product
{
	public Product(String productId, String description, String value)
	{
		super();
		this.productId = productId;
		this.description = description;
		this.value = value;
	}

	public String getDescription()
	{
		return description;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getRowNumber()
	{
		return productId;
	}

	private String	productId;

	private String	description;

	private String	value;
}
