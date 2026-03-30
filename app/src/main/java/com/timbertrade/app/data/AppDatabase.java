package com.timbertrade.app.data;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.timbertrade.app.models.InventoryItem;
import com.timbertrade.app.models.Report;

@Database(entities = {InventoryItem.class, Report.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InventoryItemDao inventoryItemDao();
    public abstract ReportDao reportDao();
}
