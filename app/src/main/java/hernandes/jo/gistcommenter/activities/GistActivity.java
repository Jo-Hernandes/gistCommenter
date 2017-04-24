package hernandes.jo.gistcommenter.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hernandes.jo.gistcommenter.R;
import hernandes.jo.gistcommenter.utils.TokenPersistence;
import hernandes.jo.gistcommenter.fragments.CommentFragment;
import hernandes.jo.gistcommenter.models.AccessToken;
import hernandes.jo.gistcommenter.models.Comment;
import hernandes.jo.gistcommenter.models.Gist;
import hernandes.jo.gistcommenter.models.Owner;
import hernandes.jo.gistcommenter.restService.DefaultConnectionError;
import hernandes.jo.gistcommenter.restService.GistAPI;
import hernandes.jo.gistcommenter.restService.ServiceCall;
import hernandes.jo.gistcommenter.utils.LoadingAnimation;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GistActivity extends AppCompatActivity implements CommentFragment.OnFragmentInteractionListener {

    public static final String EXTRA_GIST = "gist_to_be_shown";
    private static final String COMMENT_TAG = "comment_tag_to_comment_box_fragment";

    private Gist gist;

    @BindView(R.id.imageview_gistHeader_profile)
    ImageView headerProfilePicture;
    @BindView(R.id.textview_gistHeader_userName)
    TextView headerProfileUserName;
    @BindView(R.id.textview_gistHeader_userType)
    TextView headerProfileUserType;
    @BindView(R.id.textview_gistHeader_userURL)
    TextView headerUserURL;
    @BindView(R.id.textview_gistHeader_gistInfo)
    TextView headerGistDescription;

    @BindView(R.id.refreshLayout_gistComments_reloadComments)
    SwipeRefreshLayout refreshComments;
    @BindView(R.id.recyclerview_gistComments_comments)
    RecyclerView gistComments;

    private GistListAdapter adapter;

    @OnClick(R.id.textview_gistHeader_userURL) public void visitUserPage(){
        String url = headerUserURL.getText().toString();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.button_gistHeader_gistButton) public void visitGistPage(){
        String url = gist.getHtmlUrl();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gist);

        this.gist = getIntent().getParcelableExtra(EXTRA_GIST);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Owner gistOwner = gist.getOwner();
        headerProfileUserName.setText(gistOwner.getLogin());
        headerProfileUserType.setText(gistOwner.getType());
        headerUserURL.setText(gistOwner.getUrl());

        Picasso.with(this)
                .load(gistOwner.getAvatarUrl())
                .placeholder(R.drawable.shape_github_icon)
                .into(headerProfilePicture);

        headerGistDescription.setText(gist.getDescription());

        adapter = new GistListAdapter();
        setupRecyclerView(adapter);
    }

    public void setupRecyclerView(final GistListAdapter adapter) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        gistComments.setLayoutManager(layoutManager);
        gistComments.setAdapter(adapter);

        updateList(adapter);

        refreshComments.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(adapter);
            }
        });
    }

    private void updateList(final GistListAdapter adapter){

        refreshComments.setRefreshing(true);
        ServiceCall.getService(GistAPI.class)
                .getGistComments(gist.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        refreshComments.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                })
                .subscribe(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        adapter.setCommentaries(new ArrayList<>(comments));
                    }
                }, new DefaultConnectionError(this));
    }

    @Override
    public void onSendCommentText(String comment) {
        popCommentFragment();


        boolean isLogged = TokenPersistence.existsUser(this);
        if (isLogged) {
            final LoadingAnimation load = new LoadingAnimation((ViewGroup) getWindow().getDecorView());
            load.start();

            JsonObject body = new JsonObject();
            body.addProperty("body", comment);

            AccessToken accessToken = TokenPersistence.retrieveAccessToken(this);

            assert accessToken != null;
            ServiceCall.getService(GistAPI.class)
                    .createGistComment(gist.getId(), body, accessToken.getAccessToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnTerminate(new Action0() {
                        @Override
                        public void call() {
                            load.stop();
                        }
                    })
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            Toast.makeText(GistActivity.this, R.string.comment_success, Toast.LENGTH_SHORT).show();
                            updateList(adapter);
                        }
                    }, new DefaultConnectionError(this));

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.attention_label)
                    .setMessage(R.string.no_authenticated_user)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onUserCancelsComment() {
        popCommentFragment();
    }

    private void popCommentFragment(){
        getSupportFragmentManager()
            .popBackStack(COMMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public class GistListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int COMMENT_TYPE = 2;
        private static final int BUTTON_TYPE = 3;

        private ArrayList<Comment> commentaries;


        GistListAdapter(){
            commentaries = new ArrayList<>();
        }


        void setCommentaries(ArrayList<Comment> commentaries) {
            this.commentaries = commentaries;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            if (viewType == COMMENT_TYPE) return new RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_gist_comment, parent, false)) {};
            else return new RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_gist_bottom, parent, false)) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == COMMENT_TYPE){
                Comment comment = commentaries.get(position);
                ((TextView) holder.itemView.findViewById(R.id.textview_gistComment_comment)).setText(comment.getBody());

                Owner owner = comment.getUser();
                if (owner != null) {
                    ((TextView) holder.itemView.findViewById(R.id.textview_gistComment_userName)).setText(owner.getLogin());
                    ((TextView) holder.itemView.findViewById(R.id.textview_gistComment_userType)).setText(owner.getType());
                    Picasso.with(GistActivity.this)
                            .load(owner.getAvatarUrl())
                            .placeholder(R.drawable.shape_github_icon)
                            .into((ImageView) holder.itemView.findViewById(R.id.imageview_gistComment_profilePic));
                }
            }
            if (holder.getItemViewType() == BUTTON_TYPE){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GistActivity.this.popCommentFragment();

                        GistActivity.this
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.anim_go_down, R.anim.anim_go_up, R.anim.anim_go_down, R.anim.anim_go_up)
                                .add(android.R.id.content, CommentFragment.newInstance())
                                .addToBackStack(COMMENT_TAG)
                                .commit();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return commentaries.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == commentaries.size())  return BUTTON_TYPE;
            else return COMMENT_TYPE;
        }
    }


}
