package je.artist

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.firebase.database.*

import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    //view objects
    internal var editTextName: EditText
    internal var spinnerGenre: Spinner
    internal var buttonAddArtist: Button
    internal var listViewArtists: ListView

    //a list to store all the artist from firebase database
    internal var artists: MutableList<Artist>

    //our database reference object
    internal var databaseArtists: DatabaseReference

    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //getting the reference of artists node
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists")

        //getting views
        editTextName = findViewById(R.id.editTextName) as EditText
        spinnerGenre = findViewById(R.id.spinnerGenres) as Spinner
        listViewArtists = findViewById(R.id.listViewArtists) as ListView

        buttonAddArtist = findViewById(R.id.buttonAddArtist) as Button

        //list to store artists
        artists = ArrayList()


        //adding an onclicklistener to button
        buttonAddArtist.setOnClickListener {
            //calling the method addArtist()
            //the method is defined below
            //this method is actually performing the write operation
            addArtist()
        }

        listViewArtists.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val artist = artists[i]

            //creating an intent
            val intent = Intent(getApplicationContext(), ArtistActivity::class.java)

            //putting artist name and id to intent
            intent.putExtra(ARTIST_ID, artist.artistId)
            intent.putExtra(ARTIST_NAME, artist.artistName)

            //starting the activity with intent
            startActivity(intent)
        }

        listViewArtists.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            val artist = artists[i]
            showUpdateDeleteDialog(artist.artistId, artist.artistName)
            true
        }
    }

    /*
     * This method is saving a new artist to the
     * Firebase Realtime Database
     * */
    private fun addArtist() {
        //getting the values to save
        val name = editTextName.text.toString().trim { it <= ' ' }
        val genre = spinnerGenre.selectedItem.toString()

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            val id = databaseArtists.push().getKey()

            //creating an Artist Object
            val artist = Artist(id, name, genre)

            //Saving the Artist
            databaseArtists.child(id).setValue(artist)

            //setting edittext to blank again
            editTextName.setText("")

            //displaying a success toast
            Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show()
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show()
        }
    }


    protected fun onStart() {
        super.onStart()
        //attaching value event listener
        databaseArtists.addValueEventListener(object : ValueEventListener() {
            fun onDataChange(dataSnapshot: DataSnapshot) {

                //clearing the previous artist list
                artists.clear()

                //iterating through all the nodes
                for (postSnapshot in dataSnapshot.getChildren()) {
                    //getting artist
                    val artist = postSnapshot.getValue(Artist::class.java)
                    //adding artist to the list
                    artists.add(artist)
                }

                //creating adapter
                val artistAdapter = ArtistList(this@MainActivity, artists)
                //attaching adapter to the listview
                listViewArtists.adapter = artistAdapter
            }

            fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun showUpdateDeleteDialog(artistId: String, artistName: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById(R.id.editTextName) as EditText
        val spinnerGenre = dialogView.findViewById(R.id.spinnerGenres) as Spinner
        val buttonUpdate = dialogView.findViewById(R.id.buttonUpdateArtist) as Button
        val buttonDelete = dialogView.findViewById(R.id.buttonDeleteArtist) as Button

        dialogBuilder.setTitle(artistName)
        val b = dialogBuilder.create()
        b.show()


        buttonUpdate.setOnClickListener {
            val name = editTextName.text.toString().trim { it <= ' ' }
            val genre = spinnerGenre.selectedItem.toString()
            if (!TextUtils.isEmpty(name)) {
                updateArtist(artistId, name, genre)
                b.dismiss()
            }
        }

        buttonDelete.setOnClickListener {
            deleteArtist(artistId)
            b.dismiss()
        }
    }


    private fun updateArtist(id: String, name: String, genre: String): Boolean {
        //getting the specified artist reference
        val dR = FirebaseDatabase.getInstance().getReference("artists").child(id)

        //updating artist
        val artist = Artist(id, name, genre)
        dR.setValue(artist)
        Toast.makeText(getApplicationContext(), "Artist Updated", Toast.LENGTH_LONG).show()
        return true
    }


    private fun deleteArtist(id: String): Boolean {
        //getting the specified artist reference
        val dR = FirebaseDatabase.getInstance().getReference("artists").child(id)

        //removing artist
        dR.removeValue()

        //getting the tracks reference for the specified artist
        val drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id)

        //removing all tracks
        drTracks.removeValue()
        Toast.makeText(getApplicationContext(), "Artist Deleted", Toast.LENGTH_LONG).show()

        return true
    }

    companion object {

        //we will use these constants later to pass the artist name and id to another activity
        val ARTIST_NAME = "net.simplifiedcoding.firebasedatabaseexample.artistname"
        val ARTIST_ID = "net.simplifiedcoding.firebasedatabaseexample.artistid"
    }
}