package com.myelth.ohi.scheduler;

import com.myelth.ohi.controller.OhiProviderController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileProcessingService {

    @Autowired
    OhiProviderController ohiProviderController;

    private String directoryPath = "C:\\myelth\\intake";
    private int expectedCount = 10;
    private String[] expectedFileNames = {"file1.txt", "file2.txt", "file3.txt"};

    //@Scheduled(fixedRate = 3600000) // Execute every hour (adjust the cron expression as needed)
    @Scheduled(fixedDelay = 20000) // Execute every 3 seconds
    public void processFiles() throws IOException {
        File directory = new File(directoryPath);
        String[] fileNames = directory.list();

        if (fileNames == null || fileNames.length != expectedCount) {
            System.out.println("Error: Expected " + expectedCount + " files, but found " + (fileNames == null ? 0 : fileNames.length) + " files.");
            return;
        }

        boolean allFilesMatch = true;
        for (String expectedFileName : expectedFileNames) {
            boolean found = false;
            for (String fileName : fileNames) {
                if (fileName.equals(expectedFileName)) {
                    found = true;
                    processFile(directoryPath + "/" + fileName); // Process the file
                    break;
                }
            }
            if (!found) {
                allFilesMatch = false;
                System.out.println("Error: File '" + expectedFileName + "' not found in the directory.");
            }
        }

        if (allFilesMatch) {
//            List<MultipartFile> multipartFiles = new ArrayList<>();
//            for (File file : directory.listFiles()) {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                MultipartFile multipartFile = new StandardMultipartFile(fileInputStream, file.length(), file.getName());
//                multipartFiles.add(multipartFile);
           // }



          //  ohiProviderController.loadProvidersToOHI(multipartFiles);
            System.out.println("All files and counts match the expected values.");
        }
    }

    private void processFile(String filePath) {

    }
}
