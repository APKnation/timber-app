package com.timbertrade.app.data;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.timbertrade.app.models.InventoryItem;

@Dao
public interface InventoryItemDao {
    @Query("SELECT * FROM inventory")
    List<InventoryItem> getAll();
    @Insert
    void insert(InventoryItem item);
    @Update
    void update(InventoryItem item);
    @Delete
    void delete(InventoryItem item);
    @Query("DELETE FROM inventory WHERE id = :id")
    void deleteById(String id);
}
