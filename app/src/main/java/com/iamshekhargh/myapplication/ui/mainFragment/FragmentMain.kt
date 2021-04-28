package com.iamshekhargh.myapplication.ui.mainFragment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.databinding.FragmentMainBinding
import com.iamshekhargh.myapplication.ui.MainActivity
import com.iamshekhargh.myapplication.utils.onTextEntered
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 7:50 PM.
 */
@AndroidEntryPoint
class FragmentMain : Fragment(R.layout.fragment_main), NotesAdapter.OnNoteClicked,
    FirebaseAuth.AuthStateListener {

    private lateinit var searchView: SearchView

    lateinit var binding: FragmentMainBinding
    private val viewModel: FragmentMainViewModel by viewModels()
    lateinit var notesAdapter: NotesAdapter
    lateinit var onlineMenuItem: MenuItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        notesAdapter = NotesAdapter(this)
        Firebase.auth.addAuthStateListener(this@FragmentMain)

        binding.apply {
            fragMainRv.adapter = notesAdapter
            fragMainFab.setOnClickListener { viewModel.fabClicked() }
            fragMainFabTestingViews.setOnClickListener { viewModel.testFabClicked() }
        }

        viewModel.fetchFromFirebase()

        swipeDownFunctions()
        itemSlideFunctionalityOnRecyclerView()
        observeNotesForChanges()
        setupEventHandling()
        setHasOptionsMenu(true)

        viewModel.listenToRepoEvents()

        setTitle()
    }

    private fun swipeDownFunctions() = binding.fragMainSwiperl.setOnRefreshListener {
        viewModel.swipePulled()
        binding.fragMainSwiperl.isRefreshing = false
        showProgressBar(false)
    }

    private fun itemSlideFunctionalityOnRecyclerView() {
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = notesAdapter.currentList[viewHolder.adapterPosition]
                viewModel.itemSwiped(note)
            }

        }).attachToRecyclerView(binding.fragMainRv)
    }

    private fun observeNotesForChanges() {
        viewModel.getNotesList().observe(viewLifecycleOwner) { notes ->
            if (notes.isEmpty()) {
                binding.fragMainEmptyList.visibility = View.VISIBLE
//                notesAdapter.submitList(arrayListOf())
            } else {
                binding.fragMainEmptyList.visibility = View.GONE
                notesAdapter.submitList(notes)
                viewModel.uploadToFirebase(notes)
            }
        }
    }

    private fun setTitle() {
        if (Firebase.auth.currentUser == null) {
            findNavController().currentDestination?.label = "My Notes"
        } else {
            findNavController().currentDestination?.label =
                Firebase.auth.currentUser.displayName + " 's Notes"
        }
    }

    private fun setupEventHandling() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    EventHandler.OpenAddNoteFragment -> {
                        val action =
                            FragmentMainDirections.actionFragmentMainToFragmentAddNote(
                                null,
                                null,
                                "New Note"
                            )
                        findNavController().navigate(action)
                    }
                    EventHandler.OpenTestingFragment -> {
                        val action = FragmentMainDirections.actionGlobalFirebaseFragment()
                        findNavController().navigate(action)
                    }
                    is EventHandler.OpenEditNoteFragment -> {
                        val action =
                            FragmentMainDirections.actionFragmentMainToFragmentAddNote(
                                null,
                                event.note,
                                "Edit Note"
                            )
                        findNavController().navigate(action)
                    }
                    EventHandler.OpenProfileFragment -> {
                        val action = FragmentMainDirections.actionGlobalFragmentProfile()
                        findNavController().navigate(action)
                    }
                    is EventHandler.ShowConfirmationSnackBar -> {
                        confirmationSnackbar(event.note)
                    }
                    is EventHandler.ShowSnackbar -> {
                        Snackbar.make(requireView(), event.text, Snackbar.LENGTH_SHORT).show()
                    }
                    is EventHandler.ProgressBarShow -> {
                        showProgressBar(event.show)
                    }
                    is EventHandler.ShowFirebaseMessage -> {
                        showNetworkMessage(event.text)
                    }
                }
            }
        }
    }

    private fun confirmationSnackbar(note: Note) {
        Snackbar.make(requireView(), "Galti se kiya kya?", Snackbar.LENGTH_LONG)
            .setAction("Haan") {
                viewModel.insertItem(note)
            }.show()
    }

    private fun showProgressBar(kya: Boolean) {
        if (kya) {
            binding.apply {
                fragMainProgressBar.visibility = View.VISIBLE
                fragMainProgressBar.isIndeterminate = true
            }
        } else {
            binding.apply {
                fragMainProgressBar.visibility = View.GONE
                fragMainProgressBar.isIndeterminate = false
            }
        }
    }

    private fun showNetworkMessage(t: String) {
        binding.apply {
            fragMainOnlineStatusText.visibility = View.VISIBLE

            fragMainOnlineStatusText.text = t
            fragMainOnlineStatusText.animate().apply {
                translationXBy(-1000f)
                scaleXBy(-0.1f)
                scaleYBy(-0.1f)
                duration = 3500
            }.withEndAction { fragMainOnlineStatusText.visibility = View.GONE }.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_activ_menu, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        onlineMenuItem = menu.findItem(R.id.menu_online)
        searchView = searchItem.actionView as SearchView

        searchView.onTextEntered { query ->
            viewModel.setSearchQuery(query)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.logoutMenuItemClicked()
//                findNavController().popBackStack()
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            R.id.menu_profile -> {
                viewModel.profileMenuItemClicked()
            }
            R.id.menu_delete_user -> {
                viewModel.deleteUserClicked()
            }
            R.id.menu_sort_by_name -> {
                viewModel.sortByNameMenuItemClicked()
            }
            R.id.menu_sort_by_date_created -> {
                viewModel.sortByDateCreatedMenuItemClicked()
            }
            R.id.menu_ascending -> {
                item.isChecked = !item.isChecked
                viewModel.ascendingOrderToggleClicked(item.isChecked)
            }
            R.id.menu_delete_all_notes -> {
                viewModel.deleteAllNotesClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun noteItemClicked(n: Note) {
        viewModel.noteItemClicked(n)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            binding.apply {
                if (onlineMenuItem != null) {
                    onlineMenuItem.isVisible = false
                }
            }
        } else {
            binding.apply {
                onlineMenuItem.isVisible = true
            }
        }
    }
}