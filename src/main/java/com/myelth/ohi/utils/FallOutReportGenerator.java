package com.myelth.ohi.utils;

import com.myelth.ohi.exceptions.ApiError;
import com.myelth.ohi.model.Provider;
import com.myelth.ohi.model.ServiceAddress;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FallOutReportGenerator {
    private FallOutReportGenerator(){

    }
    public static void generateProviderFallOut(List<ApiError> failedRecords) {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream("C:\\myelth\\Error_files\\provider_Fallout_Report.xlsx")) {
            Sheet sheet = workbook.createSheet("Provider Fall out Data");
            List<String> headers = Arrays.asList(
                    "Provider Code",
                    "Provider Name",
                    "Provider Address 1",
                    "Provider Address 2",
                    "City",
                    "State",
                    "Zip",
                    "National Provider Identifier (NPI)",
                    "Tax Identification Number (TIN)",
                    "Provider Type",
                    "Provide Effective Date",
                    "Fallout Reason Code",
                    "Fallout Reason Description"
            );

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }
            int rowNumber = 1;
            for (ApiError apiError : failedRecords) {
                Provider provider = (Provider) apiError.getData();
                Row row = sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(provider.getCode());
                row.createCell(1).setCellValue(provider.getName());
                ServiceAddress address = new ServiceAddress();
                if(provider.getBSC_PROVIDER_TYPE() !=null && provider.getBSC_PROVIDER_TYPE().getValue().equalsIgnoreCase("P")){
                    address = provider.getRenderingAddressList().get(0).getServiceAddress();
                }else{
                    address = provider.getServiceAddressList().get(0);
                }
                row.createCell(2).setCellValue(address.getStreet());
                row.createCell(3).setCellValue(address.getHouseNumber());
                row.createCell(4).setCellValue(address.getCity());
                row.createCell(5).setCellValue(address.getCountryRegion().getCode());
                row.createCell(6).setCellValue(address.getPostalCode());
                row.createCell(7).setCellValue(provider.getNpi());
                row.createCell(8).setCellValue(provider.getBscTIN());
                row.createCell(9).setCellValue(provider.getBSC_PROVIDER_TYPE().getValue());
                row.createCell(10).setCellValue(provider.getStartDate());
                row.createCell(11).setCellValue(apiError.getCode());
                row.createCell(12).setCellValue(apiError.getMessage());
                rowNumber++;
            }
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


