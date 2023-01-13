package app.omnivore.omnivore.persistence.entities

import androidx.room.*
import java.time.LocalDate
import java.util.Date

@Entity
data class Highlight(
  @PrimaryKey val highlightId: String,
  val annotation: String?,
  val createdAt: String?,
  val createdByMe: Boolean,
  val markedForDeletion: Boolean, // default false
  val patch: String,
  val prefix: String?,
  val quote: String,
  val serverSyncStatus: Int, // default 0
  val shortId: String,
  val suffix: String?,
  val updatedAt: String?

  // has many SavedItemLabels (inverse: labels have many highlights)
  // has one savedItem (inverse: savedItem has many highlights
  // has a UserProfile (no inverse)
)

@Entity(
  primaryKeys = ["highlightId", "savedItemId"],
  foreignKeys = [
    ForeignKey(
      entity = Highlight::class,
      parentColumns = arrayOf("highlightId"),
      childColumns = arrayOf("highlightId"),
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = SavedItem::class,
      parentColumns = arrayOf("savedItemId"),
      childColumns = arrayOf("savedItemId"),
      onDelete = ForeignKey.CASCADE
    )
  ]
)
data class SavedItemAndHighlightCrossRef(
  val highlightId: String,
  val savedItemId: String
)

@Dao
interface SavedItemAndHighlightCrossRefDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(items: List<SavedItemAndHighlightCrossRef>)
}

data class SavedItemWithLabelsAndHighlights(
  @Embedded val savedItem: SavedItem,

  @Relation(
    parentColumn = "savedItemId",
    entityColumn = "savedItemLabelId",
    associateBy = Junction(SavedItemAndSavedItemLabelCrossRef::class)
  )
  val labels: List<SavedItemLabel>,

  @Relation(
    parentColumn = "savedItemId",
    entityColumn = "highlightId",
    associateBy = Junction(SavedItemAndHighlightCrossRef::class)
  )
  val highlights: List<Highlight>
)

@Dao
interface HighlightDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(items: List<Highlight>)
}
