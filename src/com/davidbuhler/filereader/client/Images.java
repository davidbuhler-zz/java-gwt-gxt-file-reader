package com.davidbuhler.filereader.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle
{
	Images	INSTANCE	= GWT.create(Images.class);

	@Source("resources/logo-big.png")
	ImageResource getLogoImage();
}