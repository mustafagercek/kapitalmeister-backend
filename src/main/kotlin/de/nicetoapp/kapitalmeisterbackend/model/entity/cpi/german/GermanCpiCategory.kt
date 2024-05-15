package de.nicetoapp.kapitalmeisterbackend.model.entity.cpi.german

import jakarta.persistence.*

@Entity
@Table(name = "german_cpi_category")
class GermanCpiCategory(
    @Id
    val id: String = "-",
    var categoryName: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parentCategory: GermanCpiCategory? = null
)
