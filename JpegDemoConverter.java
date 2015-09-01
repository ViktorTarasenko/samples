package ru.demo.impl;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.budetsdelano.startup.service.configuration.ImageConfiguration;
import ru.budetsdelano.startup.service.content.exception.StorageException;
import ru.budetsdelano.startup.service.demo.DemoConverter;
import ru.budetsdelano.startup.service.image.ImageProcessor;

public class JpegDemoConverter implements DemoConverter{
	@Autowired
	@Qualifier("jpegImageProcessor")
	private ImageProcessor processor;
	@Autowired
	private ImageConfiguration config;
    public InputStream convert(InputStream input) throws StorageException {
		try {
			return processor.scale(processor.changeCompression(input, config.getDemoCompressionQuality().floatValue()),config.getMaxDemoWidth(), config.getMaxDemoHeight()).getInputStream();
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

}
