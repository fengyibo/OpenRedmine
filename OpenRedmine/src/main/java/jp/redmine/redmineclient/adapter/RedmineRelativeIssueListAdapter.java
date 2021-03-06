package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.form.RedmineRelationListItemForm;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class RedmineRelativeIssueListAdapter extends RedmineDaoAdapter<RedmineIssueRelation, Long, DatabaseCacheHelper>  {
	private static final String TAG = RedmineRelativeIssueListAdapter.class.getSimpleName();
	protected Integer connection_id;
	protected Integer issue_id;
	protected Dao<RedmineIssue, Long> daoIssue;



	public RedmineRelativeIssueListAdapter(DatabaseCacheHelper helper, Context context) {
		super(helper, context, RedmineIssueRelation.class);
		try {
			daoIssue = helper.getDao(RedmineIssue.class);
		} catch (SQLException e) {
			Log.e(TAG, TAG, e);
		}
	}

	public void setupParameter(int connection, int issue){
		connection_id = connection;
		issue_id = issue;
	}

    @Override
	public boolean isValidParameter(){
		if(issue_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return R.layout.issuerelationitem;
	}

	@Override
	protected void setupView(View view, RedmineIssueRelation data) {
		RedmineRelationListItemForm form;
		if(view.getTag() != null && view.getTag() instanceof RedmineRelationListItemForm){
			form = (RedmineRelationListItemForm)view.getTag();
		} else {
			form = new RedmineRelationListItemForm(view);
		}
		form.setValue(data);
	}

	@Override
	protected RedmineIssueRelation getDbItem(int position){
		RedmineIssueRelation rel = super.getDbItem(position);
		try {
			RedmineIssue issue = daoIssue.queryForFirst(
				RedmineIssueModel.builderByIssue(daoIssue, connection_id, rel.getTargetIssueId(issue_id)).prepare()
				);
			if(issue != null)
				rel.setIssue(issue);
		} catch (SQLException e) {
			Log.e(TAG, "getDbItem", e);
		}
		return rel;
	}
	@Override
	protected QueryBuilder<RedmineIssueRelation, Long> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineIssueRelation, Long> builder = dao.queryBuilder();
		Where<RedmineIssueRelation,Long> where = builder.where()
				.eq(RedmineIssueRelation.ISSUE_ID,	issue_id)
				.or()
				.eq(RedmineIssueRelation.ISSUE_TO_ID,	issue_id)
				.and()
				.eq(RedmineIssueRelation.CONNECTION, connection_id)
				;
		builder.setWhere(where);
		builder.orderBy(RedmineIssueRelation.RELATION_ID, true);
		return builder;
	}

	@Override
	protected long getDbItemId(RedmineIssueRelation item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}



}
