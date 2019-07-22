package je.artist

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by Belal on 2/26/2017.
 */
@IgnoreExtraProperties
class Artist {
    val artistId: String
    val artistName: String
    val artistGenre: String

    constructor() {
        //this constructor is required
    }

    constructor(artistId: String, artistName: String, artistGenre: String) {
        this.artistId = artistId
        this.artistName = artistName
        this.artistGenre = artistGenre
    }
}