package je.artist

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TrackList(private val context: Activity, internal var tracks: List<Track>) :
    ArrayAdapter<Track>(context, R.layout.layout_artist_list, tracks) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true)

        val textViewName = listViewItem.findViewById(R.id.textViewName) as TextView
        val textViewRating = listViewItem.findViewById(R.id.textViewGenre) as TextView

        val track = tracks[position]
        textViewName.text = track.trackName
        textViewRating.text = track.rating.toString()

        return listViewItem
    }
}