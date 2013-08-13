package jp.redmine.redmineclient.fragment;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineTimeEntryListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.param.IssueArgument;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TimeEntryList extends OrmLiteListFragment<DatabaseCacheHelper> {
	private RedmineTimeEntryListAdapter adapter;
	private View mFooter;
	private OnArticleSelectedListener mListener;

	public interface OnArticleSelectedListener {
		public void onTimeEntryList(int connectionid, int issueid);
		public void onTimeEntrySelected(int connectionid, int issueid, int timeentryid);
		public void onTimeEntryEdit(int connectionid, int issueid, int timeentryid);
		public void onTimeEntryAdd(int connectionid, int issueid);
	}
	public TimeEntryList(){
		super();
	}

	static public TimeEntryList newInstance(IssueArgument intent){
		TimeEntryList instance = new TimeEntryList();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			mListener = ((ActivityInterface)activity).getHandler(OnArticleSelectedListener.class);
		}
		if(mListener == null){
			//setup empty events
			mListener = new OnArticleSelectedListener() {

				@Override
				public void onTimeEntryList(int connectionid, int issueid) {}

				@Override
				public void onTimeEntrySelected(int connectionid, int issueid, int timeentryid) {}

				@Override
				public void onTimeEntryEdit(int connectionid, int issueid, int timeentryid) {}

				@Override
				public void onTimeEntryAdd(int connectionid, int issueid) {}
			};
		}

	}
	@Override
	public void onDestroyView() {
		setListAdapter(null);
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addFooterView(mFooter);

		adapter = new RedmineTimeEntryListAdapter(getHelper());
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);
		onRefresh(true);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_footer,null);
		mFooter.setVisibility(View.GONE);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		ListView listView = (ListView) parent;
		Object item =  listView.getItemAtPosition(position);
		if(item == null || !(item instanceof RedmineTimeEntry))
			return;
		RedmineTimeEntry entry = (RedmineTimeEntry)item;
		mListener.onTimeEntrySelected(entry.getConnectionId(), entry.getIssueId(), entry.getTimeentryId());
	}

	protected void onRefresh(boolean isFetch){
		IssueArgument intent = new IssueArgument();
		intent.setArgument(getArguments());
		int connectionid = intent.getConnectionId();

		adapter.setupParameter(connectionid,intent.getIssueId());
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate( R.menu.timeentry_view, menu );
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				this.onRefresh(true);
				return true;
			}
			case R.id.menu_access_addnew:
			{
				IssueArgument intent = new IssueArgument();
				intent.setArgument(getArguments());
				mListener.onTimeEntryAdd(intent.getConnectionId(), intent.getIssueId());
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
