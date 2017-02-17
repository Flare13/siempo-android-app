package co.minium.launcher3.main;


import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.view.ActionProvider;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.contact.PhoneNumbersAdapter;
import co.minium.launcher3.event.CreateNoteEvent;
import co.minium.launcher3.event.SearchLayoutEvent;
import co.minium.launcher3.helper.ActivityHelper;
import co.minium.launcher3.token.TokenCompleteType;
import co.minium.launcher3.token.TokenItem;
import co.minium.launcher3.token.TokenItemType;
import co.minium.launcher3.token.TokenManager;
import co.minium.launcher3.token.TokenRouter;
import co.minium.launcher3.token.TokenUpdateEvent;
import co.minium.launcher3.token.TokenParser;
import co.minium.launcher3.ui.SearchLayout;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @ViewById
    SearchLayout searchLayout;

    @ViewById
    CardView listViewLayout;

    @ViewById
    CardView afterEffectLayout;

    @ViewById
    TextView txtAfterEffect;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    TokenManager manager;

    @Bean
    TokenRouter router;

    @Bean
    TokenParser parser;

    private MainListAdapter adapter;

    private MainFragmentMediator mediator;


    public MainFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        listViewLayout.setVisibility(View.GONE);
        afterEffectLayout.setVisibility(View.GONE);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mediator = new MainFragmentMediator(this);
        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) txtSearchBox2(true);
                else txtSearchBox2(false);
            }
        });
        loadData();
    }

    @Background
    void loadData() {
        mediator.loadData();
        loadView();
    }

    @UiThread
    void loadView() {
        if (getActivity() != null) {
            adapter = new MainListAdapter(getActivity(), mediator.getItems());
            listView.setAdapter(adapter);
            adapter.getFilter().filter(manager.getCurrent().getTitle());
        }
    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        if (listView.getAdapter() instanceof PhoneNumbersAdapter) {
            mediator.listItemClicked2(router, position);
        } else {
            mediator.listItemClicked(router, position);
        }

    }

    public MainListAdapter getAdapter() {
        return adapter;
    }

    @Subscribe
    public void searchLayoutEvent(SearchLayoutEvent event) {
        if (!event.getString().isEmpty()) afterEffectLayout.setVisibility(View.INVISIBLE);
        parser.parse(event.getString());
        adapter.getFilter().filter(manager.getCurrent().getTitle());
    }

    @Subscribe
    public void tokenManagerEvent(TokenUpdateEvent event) {
        TokenItem current = manager.getCurrent();

        if (current.getCompleteType() == TokenCompleteType.FULL) {
            adapter.getFilter().filter("");
        } else if (current.getItemType() == TokenItemType.CONTACT) {
            if (current.getCompleteType() == TokenCompleteType.HALF) {
                mediator.contactNumberPicker(Integer.parseInt(current.getExtra1()));
            } else {
                mediator.contactPicker();
            }
        } else if (current.getItemType() == TokenItemType.DATA) {
            adapter.getFilter().filter(current.getTitle());
        }
    }

    void txtSearchBox2(boolean isUp) {
        Tracer.v("searchLayout clicked " + searchLayout.getY());
        if (isUp && searchLayout.getY() < 100) return;

        final float direction = (isUp) ? -1 : 1;
        final float yDelta = UIUtils.getScreenHeight(getActivity())/2 - (2 * searchLayout.getHeight());
        final float yPos = searchLayout.getY();
        final int layoutTopOrBottomRule = (isUp) ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_IN_PARENT;

        final Animation animation = new TranslateAnimation(0,0,0, yDelta * direction);

        animation.setDuration(160);

        animation.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

                // fix flicking
                // Source : http://stackoverflow.com/questions/9387711/android-animation-flicker
                TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                anim.setDuration(1);
                searchLayout.startAnimation(anim);


                //set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(searchLayout.getLayoutParams());
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(layoutTopOrBottomRule);
                searchLayout.setLayoutParams(params);
            }
        });

        searchLayout.startAnimation(animation);
    }

    @Click
    void txtAfterEffect() {
        String id = (String) txtAfterEffect.getTag();
        if (id.equals("1")) {
            new ActivityHelper(getActivity()).openNotesApp();
        }
    }

    @Subscribe
    public void mainListAdapterEvent(MainListAdapterEvent event) {
        if (event.getDataSize() == 0)
            listViewLayout.setVisibility(View.GONE);
        else
            listViewLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void createNoteEvent(CreateNoteEvent event) {
        txtAfterEffect.setText("{fa-floppy-o}  View saved note");
        afterEffectLayout.setVisibility(View.VISIBLE);
        txtAfterEffect.setTag("1");
    }
}
