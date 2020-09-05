package `in`.afckstechnologies.afcksclassmusic.models

class ClassMusicDAO {
    private var id: String = ""
    private var category: String = ""
    private var ring_tone_path: String = ""
    private var volume: String = ""
    private var looping: String = ""
    private var date_time: String = ""
    private var isSelected = false
    constructor()
    constructor(
        id: String,
        category: String,
        ring_tone_path: String,
        volume: String,
        date_time: String,
        isSelected: Boolean
    ) {
        this.id = id
        this.category = category
        this.ring_tone_path = ring_tone_path
        this.volume = volume
        this.date_time = date_time
        this.isSelected = isSelected
    }

    constructor(
        id: String,
        category: String,
        volume: String,
        looping: String,
        isSelected: Boolean
    ) {
        this.id = id
        this.category = category
        this.volume = volume
        this.looping = looping
        this.isSelected = isSelected
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id!!
    }

    fun getCategory(): String? {
        return category
    }

    fun setCategory(category: String?) {
        this.category = category!!
    }

    fun getRing_tone_path(): String? {
        return ring_tone_path
    }

    fun setRing_tone_path(ring_tone_path: String?) {
        this.ring_tone_path = ring_tone_path!!
    }

    fun getVolume(): String? {
        return volume
    }

    fun setVolume(volume: String?) {
        this.volume = volume!!
    }

    fun getDate_time(): String? {
        return date_time
    }

    fun setDate_time(date_time: String?) {
        this.date_time = date_time!!
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }
    fun getLooping(): String? {
        return looping
    }

    fun setLooping(looping: String?) {
        this.looping = looping!!
    }
}
