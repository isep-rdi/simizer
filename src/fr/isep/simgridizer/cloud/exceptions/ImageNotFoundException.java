package fr.isep.simgridizer.cloud.exceptions;

public class ImageNotFoundException extends Exception {

	public ImageNotFoundException(String imgId) {
		super(imgId + " not found");
	}

}
