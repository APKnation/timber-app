package com.timbertrade.app.data;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.timbertrade.app.models.Order;
import com.timbertrade.app.models.InventoryItem;
import com.timbertrade.app.models.Report;

@Database(entities = {Order.class, InventoryItem.class, Report.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OrderDao orderDao();
    public abstract InventoryItemDao inventoryItemDao();
    public abstract ReportDao reportDao();
}
