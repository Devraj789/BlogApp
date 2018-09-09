package com.devraj.blogapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {
    RecyclerView blog_list_view;
    List<BlogPostModel> blog_list;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    BlogRecyclerAdapter blogRecyclerAdapter;

    DocumentSnapshot lastvisible;

    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.recyclerview);

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(blogRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    //this will only fire outif it is in bottom
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

                        String desc = lastvisible.getString("desc");
                        Toast.makeText(container.getContext(), "Reached:" + desc, Toast.LENGTH_SHORT).show();
                        loadMorePost();

                    } else {

                    }

                }
            });

            //loading for 1stpage with pagination 3
            Query firstQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    lastvisible = documentSnapshots.getDocuments()
                            .get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            BlogPostModel blogPostModel = doc.getDocument().toObject(BlogPostModel.class);
//                            if (isFirstPageFirstLoad) {
                            blog_list.add(blogPostModel);
//                            } else {
//                                blog_list.add(0, blogPostModel);
//                            }

                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }

//                    isFirstPageFirstLoad = false;
                }


            });
        }
        return view;
    }


    public void loadMorePost() {
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastvisible)
                .limit(3);
        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    lastvisible = documentSnapshots.getDocuments()
                            .get(documentSnapshots.size() - 1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            BlogPostModel blogPostModel = doc.getDocument().toObject(BlogPostModel.class);
                            blog_list.add(blogPostModel);
                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }

        });
    }

}
