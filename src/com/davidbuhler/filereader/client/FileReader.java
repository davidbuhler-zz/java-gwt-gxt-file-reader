package com.davidbuhler.filereader.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.davidbuhler.filereader.shared.FileReaderUtil;
import com.davidbuhler.filereader.shared.Product;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;

public class FileReader implements IsWidget, EntryPoint
{
	interface ProductProperties extends PropertyAccess<Product>
	{
		ValueProvider<Product, String> description();

		@Path(PRODUCT_ID)
		ModelKeyProvider<Product> key();

		ValueProvider<Product, String> value();
	}

	private static final String	DESCRIPTION			= "Description";

	private static final String	ERROR				= "ERROR";

	private static final String	FILE				= "File";

	private static final String	FILE_READER			= "File Reader";

	private static final String	LOADING				= "Loading";

	private static Logger		logger				= Logger.getLogger("FileReaderLogger");

	private static final String	NO_RESULTS			= "No Results";

	private static final String	PRODUCT_ID			= "productId";

	private static final String	RESET				= "Reset";

	private static final String	UPLOAD				= "Upload";

	private static final String	UPLOAD_ACTION_URL	= GWT.getModuleBaseURL() + "upload";

	private static final String	VALUE				= "Value";

	private MessageBox			box;

	private Grid<Product>		grid;

	private ProductProperties	props				= GWT.create(ProductProperties.class);

	private ListStore<Product>	store				= new ListStore<Product>(props.key());

	@Override
	public Widget asWidget()
	{
		ContentPanel contentPanel = new ContentPanel();
		final Image logoImage = new Image(Images.INSTANCE.getLogoImage());
		CenterLayoutContainer centerLayoutContainer = new CenterLayoutContainer();
		contentPanel.setBodyStyle("padding: 0px");
		contentPanel.setShadow(true);
		contentPanel.setHeaderVisible(false);
		contentPanel.setHeadingText(FILE_READER);
		contentPanel.setPixelSize(800, 620);
		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		contentPanel.setWidget(verticalLayoutContainer);
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setPixelSize(800, 10);
		verticalPanel.setStyleName("blue_divider");
		verticalLayoutContainer.add(logoImage);
		verticalLayoutContainer.add(verticalPanel);
		verticalLayoutContainer.add(horizontalLayoutContainer);
		horizontalLayoutContainer.add(createFileBrowser(), new HorizontalLayoutData(-1, -1, new Margins(3)));
		horizontalLayoutContainer.add(createDataGrid(), new HorizontalLayoutData(-1, 500, new Margins(3)));
		centerLayoutContainer.add(contentPanel);
		return centerLayoutContainer;
	}

	private ContentPanel createDataGrid()
	{
		ColumnConfig<Product, String> cc2 = new ColumnConfig<Product, String>(props.description(), 125, DESCRIPTION);
		ColumnConfig<Product, String> cc3 = new ColumnConfig<Product, String>(props.value(), 75, VALUE);
		cc3.setCell(new SimpleSafeHtmlCell<String>(new AbstractSafeHtmlRenderer<String>()
		{
			@Override
			public SafeHtml render(String s)
			{
				if (FileReaderUtil.IsNullOrEmptyString(s))
				{
					return SafeHtmlUtils.fromString("");
				}
				String moneyFormatted = com.google.gwt.i18n.client.NumberFormat.getCurrencyFormat().format(Double.parseDouble(s));
				return SafeHtmlUtils.fromString(moneyFormatted);
			}
		}));
		List<ColumnConfig<Product, ?>> l = new ArrayList<ColumnConfig<Product, ?>>();
		l.add(cc2);
		l.add(cc3);
		ColumnModel<Product> cm = new ColumnModel<Product>(l);
		grid = new Grid<Product>(store, cm);
		grid.getView().setForceFit(true);
		grid.setBorders(true);
		grid.getView().setEmptyText(NO_RESULTS);
		// grid.setLoadMask(true);
		FramedPanel cp = new FramedPanel();
		cp.setHeaderVisible(false);
		cp.setCollapsible(true);
		cp.setAnimCollapse(true);
		cp.setWidget(grid);
		cp.setWidth(450);
		return cp;
	};

	private FramedPanel createFileBrowser()
	{
		FramedPanel panel = new FramedPanel();
		panel.setButtonAlign(BoxLayoutPack.END);
		panel.setPixelSize(330, 200);
		panel.getElement().setMargins(0);
		panel.setHeaderVisible(false);
		panel.addStyleName("margin-10");
		final FormPanel form = new FormPanel();
		form.setAction(UPLOAD_ACTION_URL);
		form.setEncoding(Encoding.MULTIPART);
		form.setMethod(Method.POST);
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		final FileUploadField fileUploadField = new FileUploadField();
		fileUploadField.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				Info.display("File Changed", "You selected " + fileUploadField.getValue());
			}
		});
		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingText("Please add your file");
		fieldSet.setCollapsible(false);
		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		fieldSet.add(verticalLayoutContainer);
		fileUploadField.setName("uploadFormElement");
		fileUploadField.setAllowBlank(false);
		p.add(new FieldLabel(fileUploadField, FILE), new VerticalLayoutData(-1, -1));
		fieldSet.add(p);
		TextButton resetButton = new TextButton(RESET);
		TextButton submitButton = new TextButton(UPLOAD);
		form.add(fieldSet);
		panel.add(form);
		panel.addButton(resetButton);
		panel.addButton(submitButton);
		resetButton.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				form.reset();
				fileUploadField.reset();
			}
		});
		submitButton.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				if (!form.isValid())
				{
					return;
				}
				grid.getElement().mask(LOADING);
				form.submit();
				form.addSubmitCompleteHandler(new SubmitCompleteHandler()
				{
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event)
					{
						grid.getElement().unmask();
						String body = event.getResults();
						List<Product> productList = new ArrayList<Product>();
						try
						{
							JSONArray response = (JSONArray) JSONParser.parseLenient(body);
							for (int i = 0; i < response.size(); i++)
							{
								JSONObject item = (JSONObject) response.get(i);
								String productId = item.get(PRODUCT_ID).isString().stringValue();
								String description = item.get(DESCRIPTION).isString().stringValue();
								String value = item.get(VALUE).isString().stringValue();
								productList.add(new Product(productId, description, value));
							}
							store.replaceAll(productList);
						} catch (UmbrellaException e)
						{
							String msg = "An error has taken place. Please try your request again";
							logger.log(Level.SEVERE, e.getMessage());
							box = new MessageBox(ERROR, msg);
							box.setIcon(MessageBox.ICONS.info());
							box.show();
						}
					}
				});
			}
		});
		return panel;
	}

	@Override
	public void onModuleLoad()
	{
		try
		{
			RootPanel.get().add(asWidget());
		} catch (Exception e)
		{
			logger.log(Level.SEVERE, e.getCause().toString());
		}
	}
}