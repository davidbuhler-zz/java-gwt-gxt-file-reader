package com.davidbuhler.filereader.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

import com.davidbuhler.filereader.shared.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileUploadServlet extends HttpServlet
{
	private static final long	serialVersionUID	= -8045560827673306825L;

	private void createCSVFromFile(HttpServletRequest req, HttpServletResponse resp) throws Exception
	{
		FileItem uploadItem = getFileItem(req);
		if (uploadItem == null)
		{
			resp.getWriter().write("NO-SCRIPT-DATA");
			return;
		}
		CSVReader reader = new CSVReader(new InputStreamReader(uploadItem.getInputStream()));
		String[] nextLine;
		List<Product> productList = new ArrayList<Product>();
		while ((nextLine = reader.readNext()) != null)
		{
			Product product = new Product(nextLine[0], nextLine[1], nextLine[2]);
			productList.add(product);
		}
		reader.close();
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(productList);
		resp.setContentType("text/html");
		resp.getWriter().write(json);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			createCSVFromFile(req, resp);
		} catch (FileUploadException | SAXException | TikaException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private FileItem getFileItem(HttpServletRequest req)
	{
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try
		{
			List<?> items = upload.parseRequest(req);
			Iterator<?> it = items.iterator();
			while (it.hasNext())
			{
				FileItem item = (FileItem) it.next();
				if (!item.isFormField() && "uploadFormElement".equals(item.getFieldName()))
				{
					return item;
				}
			}
		} catch (FileUploadException e)
		{
			return null;
		}
		return null;
	}
}
