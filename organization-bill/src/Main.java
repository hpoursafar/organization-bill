import model.Bill;
import model.Organization;
import model.OrganizationEnum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
//        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\bill\\01_bill"))) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/01_bill"))) {
            List<String> list = reader.lines().collect(Collectors.toList());
            String correctLine = "";
            List<String> correctLines = new ArrayList<>();
            for (int i = 0; i <= list.size(); i++) {
                if (parsingValidation(list, i))
                    correctLine = organizationValidation(list, i);
                if (correctLine != null)
                    correctLines.add(correctLine);
            }
            generateReport(correctLines);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean parsingValidation(List<String> list, int i) {
        int lineNumber;
        String billId;
        try {
            var checkLine = list.get(i).split(",")[2];
            billId = list.get(i).split(",")[0].split(":")[1];
            System.out.println(billId);
            return true;
        } catch (Exception e) {
            lineNumber = i;
            lineNumber += 1;
            writeFile("line " + lineNumber + ": not parsed\n", Path.of("C:\\bill\\out.validate.txt"));
            System.out.println("not parsed");
            return false;
        }
    }

    private static String organizationValidation(List<String> list, int i) {
        int lineNumber;
        String billId;
        String orgType = "";
        String orgCode = "";
        try {
            var checkLine = list.get(i).split(",")[2];
            billId = list.get(i).split(",")[0].split(":")[1].replaceAll("\"", "");
            orgType = billId.substring(billId.length() - 2, billId.length() - 1);
            orgCode = billId.substring(billId.length() - 5, billId.length() - 2);
            Path path = Path.of("C:\\bill\\out.validate.txt");
            if (!checkOrganization(Integer.parseInt(orgCode), orgType)) {
                lineNumber = i;
                lineNumber += 1;
                writeFile("line " + lineNumber + ": invalid organization " + orgType + " " + orgCode + "\n", path);
                return null;
            }
            if (!checkEnabledOrganization(Integer.parseInt(orgCode), orgType)) {
                lineNumber = i;
                lineNumber += 1;
                writeFile("line " + lineNumber + ": organization " + OrganizationEnum.find(orgType).name() + " " + orgCode + " not enabled.\n", path);
                return null;
            }
            return list.get(i);
        } catch (Exception e) {
            return null;
        }
    }

    private static void writeFile(String text, Path path) {
        try {
            Files.writeString(path, text, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Handling any I/O exceptions
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static boolean checkOrganization(int code, String type) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/02_organization"))) {
            List<String> list = reader.lines().collect(Collectors.toList());
            List<Organization> orgs = getOrganizations(list);
            return orgs.stream().anyMatch(item -> OrganizationEnum.findByName(item.getOrganizationType()).getOrganizationType().equals(type)
                    && item.getCompanyCode() == code);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkEnabledOrganization(int code, String type) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/02_organization"))) {
            List<String> list = reader.lines().collect(Collectors.toList());
            List<Organization> orgs = getOrganizations(list);
            return orgs.stream().anyMatch(item -> OrganizationEnum.findByName(item.getOrganizationType()).getOrganizationType().equals(type)
                    && item.getCompanyCode() == code && item.getEnable() == 1);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Organization> getOrganizations(List<String> list) {
        List<Organization> orgs = new ArrayList<>();
        for (int i = 0; i <= list.size(); i++) {
            Organization org = new Organization();
            try {
                String[] record = list.get(i).split(",");
                org.setAccount(Long.parseLong(record[3]));
                org.setEnable(Integer.parseInt(record[1]));
                org.setName(record[4]);
                org.setOrganizationType(record[0]);
                org.setCompanyCode(Integer.parseInt(record[2]));
                orgs.add(org);
            } catch (Exception e) {
//                System.err.println("An error occurred: " + e.getMessage());
            }

        }
        return orgs;
    }

    private static void generateReport(List<String> reportLines) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/02_organization"))) {
            List<String> list = reader.lines().collect(Collectors.toList());
            List<Organization> orgs = getOrganizations(list);
            Long count = 0L;
            for (Organization org : orgs) {
                if (org.getEnable() == 1) {
                    List<Bill> billsList = getBills(reportLines);
                    count = billsList.stream().filter(item -> OrganizationEnum.findByName(org.getOrganizationType())
                            .getOrganizationType().equals(item.getBillId().toString().substring(item.getBillId().toString().length() - 2, item.getBillId().toString().length() - 1))
                            && Integer.parseInt(item.getBillId().toString().substring(item.getBillId().toString().length() - 5, item.getBillId().toString().length() - 2)) == org.getCompanyCode()).count();
                    Long sum = billsList.stream().filter(item -> OrganizationEnum.findByName(org.getOrganizationType())
                            .getOrganizationType().equals(item.getBillId().toString().substring(item.getBillId().toString().length() - 2, item.getBillId().toString().length() - 1))
                            && Integer.parseInt(item.getBillId().toString().substring(item.getBillId().toString().length() - 5, item.getBillId().toString().length() - 2)) == org.getCompanyCode()).mapToLong(Bill::getAmount).sum();

                    writeFile(org.getAccount().toString() + " " + count + " " + sum + "\n", Path.of("C:\\bill\\out.report1.txt"));
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Bill> getBills(List<String> list) {
        List<Bill> bills = new ArrayList<>();
        for (int i = 0; i <= list.size(); i++) {
            Bill bill = new Bill();
            try {
                String[] record = list.get(i).split(",");
                bill.setBillId(Long.parseLong(record[0].split(":")[1].replaceAll("\"", "")));
                bill.setAmount(Long.parseLong(record[2].split(":")[1].replaceAll("\"", "").replaceAll("}", "")));
                bill.setPaymentId(Long.parseLong(record[1].split(":")[1].replaceAll("\"", "")));
                bills.add(bill);
            } catch (Exception e) {
//                System.err.println("An error occurred: " + e.getMessage());
            }

        }
        return bills;
    }
}