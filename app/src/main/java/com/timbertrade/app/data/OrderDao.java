package com.timbertrade.app.data;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.timbertrade.app.models.Order;

@Dao
public interface OrderDao {
    @Query("SELECT * FROM orders")
    List<Order> getAll();
    @Insert
    void insert(Order order);
    @Update
    void update(Order order);
    @Delete
    void delete(Order order);
    @Query("DELETE FROM orders WHERE id = :id")
    void deleteById(String id);
}
