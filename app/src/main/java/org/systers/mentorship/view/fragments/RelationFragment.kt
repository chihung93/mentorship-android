package org.systers.mentorship.view.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_relation.*
import org.systers.mentorship.R
import android.text.method.ScrollingMovementMethod
import android.view.View
import kotlinx.android.synthetic.main.fragment_members.*
import org.systers.mentorship.remote.responses.MentorshipRelationResponse
import org.systers.mentorship.utils.EXTENDED_DATE_FORMAT
import org.systers.mentorship.utils.convertUnixTimestampIntoStr
import org.systers.mentorship.view.activities.MainActivity
import org.systers.mentorship.viewmodels.RelationViewModel

/**
 * The fragment is responsible present the current mentorship relation  details
 */
class RelationFragment : BaseFragment() {

    companion object {
        /**
         * Creates an instance of RelationFragment
         */
        fun newInstance() = RelationFragment()
        val TAG = RelationFragment::class.java.simpleName
    }

    private lateinit var relationViewModel: RelationViewModel
    private val activityCast by lazy { activity as MainActivity }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_relation
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        relationViewModel = ViewModelProviders.of(this).get(RelationViewModel::class.java)
        relationViewModel.successfulGet.observe(this, Observer {
            successful ->
            activityCast.hideProgressDialog()
            if (successful != null) {
                if (successful) {
                    populateView(relationViewModel.mentorshipRelation)
                } else {
                    view?.let {
                        Snackbar.make(it, relationViewModel.message, Snackbar.LENGTH_LONG).show()

                    }
                }
            }
        })
        relationViewModel.successfulCancel.observe(this, Observer {
            successful ->
            activityCast.hideProgressDialog()
            if (successful != null) {
                if (successful) {
                    populateView(relationViewModel.mentorshipRelation)
                } else {
                    view?.let {
                        Snackbar.make(it, relationViewModel.message, Snackbar.LENGTH_LONG).show()

                    }
                }
            }
        })
        activityCast.showProgressDialog(getString(R.string.fetching_users))



        tvRelationNotes.movementMethod = ScrollingMovementMethod()
        relationViewModel.getCurrentRelationDetails()
    }

    private fun populateView(relationResponse: MentorshipRelationResponse) {

        // TODO this is a way to prevent crash when a user is not in a relation
        // and receives just a simple message

        // Empty state
        if (relationResponse.mentor == null) {
            tvNoCurrentRelation.visibility = View.VISIBLE

            btnCancelRelation.visibility = View.GONE
            tvEndDateLabel.visibility = View.GONE
            tvNotesLabel.visibility = View.GONE
            tvMenteeLabel.visibility = View.GONE
            tvMentorLabel.visibility = View.GONE
        } else {
            tvNoCurrentRelation.visibility = View.GONE
            tvMentorName.text = relationResponse.mentor.name
            tvMenteeName.text = relationResponse.mentee.name
            tvEndDate.text = convertUnixTimestampIntoStr(
                    relationResponse.endAtTimestamp, EXTENDED_DATE_FORMAT)
            tvRelationNotes.text = relationResponse.notes

            btnCancelRelation.visibility = View.VISIBLE
            btnCancelRelation.setOnClickListener {
                relationViewModel.cancelMentorshipRelation(relationResponse.id)
            }
        }

    }
}
