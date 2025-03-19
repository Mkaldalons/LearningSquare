package hbv601g.learningsquare.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey val id: Int = 0, // Þurfum að hafa id sem er alltaf 0 til að hafa bara einn user í einu
    @ColumnInfo val userName: String,
    @ColumnInfo val name: String,
    @ColumnInfo val email: String,
    @ColumnInfo val password: String,
    @ColumnInfo val isInstructor: Boolean,
    @ColumnInfo val profileImagePath: String?,
    @ColumnInfo val recoveryEmail: String?
)