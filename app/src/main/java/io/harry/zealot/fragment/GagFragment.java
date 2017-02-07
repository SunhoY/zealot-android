package io.harry.zealot.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.R;
import io.harry.zealot.ZealotApplication;

public class GagFragment extends Fragment {
    public static final String GAG_IMAGE_URI = "gagImageUri";
    @Inject
    Picasso picasso;

    @BindView(R.id.gag_image)
    ImageView gagImage;

    private Uri gagImageUri;

    public static GagFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(GAG_IMAGE_URI, uri);

        GagFragment fragment = new GagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ZealotApplication) getActivity().getApplication()).getZealotComponent().inject(this);

        gagImageUri = (Uri) getArguments().get(GAG_IMAGE_URI);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gag, null);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        if(gagImageUri != null) {
            picasso.load(gagImageUri).into(gagImage);
        }

        super.onResume();
    }
}
