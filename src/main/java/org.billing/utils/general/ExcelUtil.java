package org.billing.utils.general;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.billing.commonpojos.ToraTrader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    /**
     * Reads an Excel or CSV file and returns data as a List of Maps. Each map represents a row,
     * with the key as the column header and the value as the cell value.
     *
     * @param filePath Path to the file (.xls, .xlsx, or .csv)
     * @param headerOn
     * @return List of Maps representing file data
     * @throws IOException If file reading fails
     */
    public static List<Map<String, String>> readExcelOrCsvWithDataTable(String filePath, boolean headerOn, String functionality) throws IOException {
        if (filePath.endsWith(".xls") || filePath.endsWith(".xlsx")) {
            return readExcel(filePath, headerOn, functionality);
        } else if (filePath.endsWith(".csv")) {
            return readCsv(filePath, headerOn, functionality);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Only .xls, .xlsx, or .csv are allowed.");
        }
    }

    /**
     * Reads an Excel file (.xls or .xlsx) and returns data as a List of Maps. Each map represents a row,
     * with the key as the column header and the value as the cell value.
     *
     * @param excelFilePath Path to the Excel file
     * @param headerOn
     * @param functionality
     * @return List of Maps representing Excel data
     * @throws IOException If file reading fails
     */
    public static List<Map<String, String>> readExcel(String excelFilePath, boolean headerOn, String functionality) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new HSSFWorkbook(fis)) { // Use HSSFWorkbook for .xls, XSSFWorkbook for .xlsx

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0); // Assumes the first row contains column headers
            int columns = headerRow.getPhysicalNumberOfCells();

            // Iterate through rows (skipping header row)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue; // Skip empty rows
                }

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < columns; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String header = headerRow.getCell(j).getStringCellValue();
                    String value = getCellValueAsString(cell);
                    rowData.put(header, value);
                }
                data.add(rowData);
            }
        }
        return data;
    }

    /**
     * Reads a CSV file and returns data as a List of Maps. Each map represents a row,
     * with the key as the column header and the value as the cell value.
     *
     * @param csvFilePath   Path to the CSV file
     * @param headerOn
     * @param functionality
     * @return List of Maps representing CSV data
     * @throws IOException If file reading fails
     */
    private static List<Map<String, String>> readCsv(String csvFilePath, boolean headerOn, String functionality) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        String tradersHeader[] = {"id", "name", "toraTraderString"};
        try (BufferedReader br = new BufferedReader(new FileReader(new File(csvFilePath)))) {
            String line;
            String[] headers = null;

            int rowIndex = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Split the line by commas
                Map<String, String> rowData = new HashMap<>();

                if (rowIndex == 0 && headerOn) {
                    // First row is treated as headers when `headerOn` is true
                    headers = values;
                } else {
                    if (headerOn && headers != null) {
                        // Populate the row using headers as keys
                        for (int i = 0; i < values.length; i++) {
                            rowData.put(headers[i].trim(), values[i].trim());
                        }
                    } else {
                        if (functionality.equalsIgnoreCase("Traders")) {
                            for (int i = 0; i < values.length; i++) {
                                rowData.put(tradersHeader[i].trim(), values[i].trim());
                            }
                        } else {
                            for (int i = 0; i < values.length; i++) {
                                rowData.put("Column" + (i + 1), values[i].trim());
                            }
                        }
                    }
                    data.add(rowData); // Add row to the list
                }
                rowIndex++;
            }
        }
        return data;
    }

    /**
     * Helper method to convert a cell's value to a String.
     *
     * @param cell Cell object from the Excel sheet
     * @return String representation of the cell value
     */
    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return Double.toString(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    /**
     * Writes a List of Maps to a .xls Excel file.
     *
     * @param data          List of Maps where each map represents a row
     * @param excelFilePath Path to the output .xls Excel file
     * @param sheetName     Name of the Excel sheet
     * @throws IOException If file writing fails
     */
    public static void writeExcel(List<Map<String, String>> data, String excelFilePath, String sheetName) throws IOException {
        Workbook workbook = new HSSFWorkbook(); // Use HSSFWorkbook for .xls files
        Sheet sheet = workbook.createSheet(sheetName);

        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data is empty. Cannot write an empty Excel file.");
        }

        // Write the header row
        Row headerRow = sheet.createRow(0);
        Map<String, String> firstRow = data.get(0);
        int columnIndex = 0;
        for (String key : firstRow.keySet()) {
            Cell cell = headerRow.createCell(columnIndex++);
            cell.setCellValue(key);
        }

        // Write the data rows
        int rowIndex = 1;
        for (Map<String, String> rowData : data) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (String value : rowData.values()) {
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(value);
            }
        }

        // Write to the file
        try (FileOutputStream fos = new FileOutputStream(new File(excelFilePath))) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }
    }

    /**
     * Converts a List<Map<String, String>> into a List<ToraTrader>.
     *
     * @param rowData List of maps representing rows of the CSV or Excel data
     * @return List of ToraTrader POJO objects
     */
    public static List<ToraTrader> mapToToraTraderList(List<Map<String, String>> rowData) {
        List<ToraTrader> traderList = new ArrayList<>();

        for (Map<String, String> row : rowData) {
            try {
                // Extract and validate required fields
                String id = row.get("id");
                String name = row.get("name");
                String toraTraderString = row.get("toraTraderString");

                if (isNullOrEmpty(id) || isNullOrEmpty(name) || isNullOrEmpty(toraTraderString)) {
                    System.err.println("Skipping row due to missing mandatory fields: " + row);
                    continue; // Skip rows with missing mandatory fields
                }

                // Create a trader object with trimmed values and add to list
                traderList.add(new ToraTrader(id.trim(), name.trim(), toraTraderString.trim()));
            } catch (Exception e) {
                // Log and skip the invalid row
                System.err.println("Error processing row: " + row + " | Error: " + e.getMessage());
            }
        }

        return traderList;
    }

    // Helper method to check for null or empty values
    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}