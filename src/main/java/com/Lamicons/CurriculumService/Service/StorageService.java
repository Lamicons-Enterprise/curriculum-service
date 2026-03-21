package com.Lamicons.CurriculumService.Service;

import java.util.Map;

public interface StorageService {
    /**
     * Generates an upload signature/URL for client-side direct upload
     * @param folderName The intended folder or prefix path (e.g., "courses/thumbnails")
     * @param fileName Optional intended file name, if supported by adapter
     * @return Map containing upload credentials (e.g., presignedUrl, or Cloudinary signature config)
     */
    Map<String, Object> generateUploadSignature(String folderName, String fileName);
    
    /**
     * Extracts or formats the final public URL by using the fileKey.
     * @param fileKey The key or public ID returned by storage after direct upload.
     * @return Full URL to access the file
     */
    String getFileUrl(String fileKey);
}