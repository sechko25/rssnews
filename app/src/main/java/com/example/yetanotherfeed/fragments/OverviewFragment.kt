package com.example.yetanotherfeed.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.yetanotherfeed.adapter.FeedListAdapter
import com.example.yetanotherfeed.adapter.ItemListener
import com.example.yetanotherfeed.databinding.FragmentOverviewBinding
import com.example.yetanotherfeed.viewmodels.OverviewViewModel

/**
 * A simple [Fragment] subclass.
 */
class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, OverviewViewModel.Factory(activity.application))
            .get(OverviewViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentOverviewBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.feedList.adapter = FeedListAdapter(ItemListener {
            findNavController().navigate(
                OverviewFragmentDirections.actionOverviewFragmentToDetailFragment(it)
            )
        })

        binding.setLifecycleOwner(this)

        viewModel.items.observe(this, Observer {
            (binding.feedList.adapter as FeedListAdapter).submitList(it)
        })

        viewModel.eventNetworkError.observe(this, Observer {
            if (it)
                Toast.makeText(this.context, "Invalid RSS url", Toast.LENGTH_SHORT).show()
        })

        viewModel.instantiateEditTxtView(binding.editLinkText)

        binding.changeLinkBtn.setOnClickListener {
            viewModel.refreshDataFromRepository(binding.editLinkText.text.toString())
        }

        return binding.root
    }


}
