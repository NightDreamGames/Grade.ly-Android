package com.NightDreamGames.Grade.ly.Misc;

import android.content.res.AssetManager;

import com.NightDreamGames.Grade.ly.Activities.MainActivity;
import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Period;
import com.NightDreamGames.Grade.ly.Calculator.Subject;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class ExcelParser {
    private static HSSFWorkbook workBook;

    public static CharSequence[] Init() throws IOException {
        InputStream myInput;
        // initialize asset manager
        AssetManager assetManager = MainActivity.sApplication.getAssets();
        //  open excel file name as Classes.xlsx
        myInput = assetManager.open("Classes.xls");
        // Create a POI File System object
        POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

        // Create a workbook using the File System
        workBook = new HSSFWorkbook(myFileSystem);

        int length = 0;
        for (int i = workBook.getNumberOfSheets() - 1; i >= 0; i--) {
            String name = workBook.getSheetName(i);
            if (name.length() <= 3)
                length++;
        }

        CharSequence[] classes = new CharSequence[length];
        int pos = 0;
        for (int i = workBook.getNumberOfSheets() - 1; i >= 0; i--) {
            String name = workBook.getSheetName(i);
            if (name.length() <= 3) {
                classes[pos] = name;
                pos++;
            }
        }
        System.out.println(Arrays.toString(classes));

        return classes;
    }

    public static void fillSubjects(String className, boolean latin, boolean chinese) {
        Manager.periodTemplate.clear();
        for (Period p : Manager.getCurrentYear().periods)
            p.subjects.clear();

        int position = 4;
        if (latin)
            position = 6;
        else if (chinese)
            position = 8;

        HSSFSheet sheet = workBook.getSheet(className);

        Iterator<Row> rowIter = sheet.rowIterator();
        rowIter.next();

        while (rowIter.hasNext()) {
            HSSFRow row = (HSSFRow) rowIter.next();
            if (!row.getCell(position).toString().isEmpty()) {
                String name = row.getCell(0).toString();
                double coefficient = Double.parseDouble(row.getCell(position).toString());

                Manager.periodTemplate.add(new Subject(name, coefficient));

                for (Period p : Manager.getCurrentYear().periods) {
                    p.subjects.add(new Subject(name, coefficient));
                }
            }
        }

        Manager.calculate();
    }
}