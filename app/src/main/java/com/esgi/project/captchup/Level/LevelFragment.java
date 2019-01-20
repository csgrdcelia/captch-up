package com.esgi.project.captchup.Level;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.esgi.project.captchup.Game.GameFragment;
import com.esgi.project.captchup.ImageProcessingFragment;
import com.esgi.project.captchup.MainActivity;
import com.esgi.project.captchup.Models.Level;
import com.esgi.project.captchup.R;
import com.esgi.project.captchup.Utils.RecyclerViewClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LevelFragment extends Fragment {

    public enum LevelFragmentType {
        FINISHED,
        UNFINISHED
    }

    public static final String LEVEL_FRAGMENT_TYPE = "levelFragmentType";
    private LevelFragmentType levelFragmentType = LevelFragmentType.UNFINISHED;
    int index = 2;
    private RecyclerView recyclerView;
    private List<Level> levels = new ArrayList<>();

    private DatabaseReference databaseReference;

    public LevelFragment() {
        // Required empty public constructor
    }

    public static LevelFragment newInstance(LevelFragmentType type) {

        Bundle args = new Bundle();
        LevelFragment fragment = new LevelFragment();
        args.putSerializable(LEVEL_FRAGMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: make this code cleaner
        try {

            levelFragmentType = (LevelFragmentType)this.getArguments().get(LEVEL_FRAGMENT_TYPE);
        }catch (Exception e) { }
        return inflater.inflate(R.layout.fragment_level, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView)getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getView().getContext(),2)); // DISPLAY 2 PER ROW

        databaseReference = FirebaseDatabase.getInstance().getReference(Level.LEVELS_ROOT);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Level level = postSnapshot.getValue(Level.class);
                    levels.add(level);
                }

                RecyclerViewClickListener listener = (view,levelId) -> {
                    MainActivity activity = (MainActivity) view.getContext();
                    Fragment myFragment = GameFragment.newInstance(levelId);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, myFragment).addToBackStack(null).commit();
                };
                recyclerView.setAdapter(new LevelAdapter(levels, listener));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        /*if(levelFragmentType == LevelFragmentType.FINISHED)
            levels = Level.getFinishedLevels();
        else
            levels = Level.getUnfinishedLevels();*/




    }
}
