package invest.megalo.data

import invest.megalo.adapter.ProfileChildListAdapter

data class ProfileParentListData(
    val section: String,
    val childListAdapters: ProfileChildListAdapter
)