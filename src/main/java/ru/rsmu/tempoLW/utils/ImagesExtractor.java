package ru.rsmu.tempoLW.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author leonid.
 */
public class ImagesExtractor {
    private Map<String, byte[]> imagesMap;

    public ImagesExtractor() {
        imagesMap = new HashMap<>();
    }

    public void extractImages( InputStream zipFileStream ) {
        ZipInputStream zipInputStream = new ZipInputStream( zipFileStream );
        try {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while ( zipEntry != null ) {
                String imageName = zipEntry.getName();
                if ( imageName.matches( ".*\\.((gif)|(jpeg)|(jpg)|(png))$" ) ) {
                    byte[] imageBytes = IOUtils.toByteArray( zipInputStream );
                    imagesMap.put( imageName, imageBytes );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getPicture( String imageName ) {
        return imagesMap.get( imageName );
    }

    public String getContentType( String imageName ) {
        if ( imageName.matches( ".*\\.((gif)|(jpeg)|(jpg)|(png)|(svg)|(svgz))$" ) ) {
            if ( imageName.matches( ".*\\.gif$" ) ) {
                return "image/gif";
            }
            else if ( imageName.matches( ".*\\.((jpeg)|(jpg))$" ) ) {
                return "image/jpeg";
            }
            else if ( imageName.matches( ".*\\.png$" ) ) {
                return "image/png";
            }
            else if ( imageName.matches( ".*\\.((svg)|(svgz))$" ) ) {
                return "image/svg+xml";
            }
        }
        return null;
    }
}
