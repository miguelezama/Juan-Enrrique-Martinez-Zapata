package je.artist

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Created by Belal on 2/26/2017.
 */

class ArtistList(private val context: Activity, internal var artists: List<Artist>) :
    ArrayAdapter<Artist>(context, R.layout.layout_artist_list, artists) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true)

        val textViewName = listViewItem.findViewById(R.id.textViewName) as TextView
        val textViewGenre = listViewItem.findViewById(R.id.textViewGenre) as TextView

        val artist = artists[position]
        textViewName.text = artist.artistName
        textViewGenre.text = artist.artistGenre

        return listViewItem
    }
}