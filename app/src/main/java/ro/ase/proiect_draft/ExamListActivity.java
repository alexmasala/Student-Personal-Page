package ro.ase.proiect_draft;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ro.ase.proiect_draft.database.AsyncTaskRunner.Callback;
import ro.ase.proiect_draft.database.services.ExamService;

public class ExamListActivity extends AppCompatActivity {

    public static final String EDIT_EXAM = "editExam";
    private static final int ADD_EXAM_REQUEST_CODE = 201;
    private static final int UPDATE_EXAM_REQUEST_CODE = 222;

    private ListView lvExam;
    private FloatingActionButton fabAddExams;

    private final List<Exam> listaExamene = new ArrayList<>();

    private ExamService examService;

    private SharedPreferences shp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_exam_list);

        lvExam = findViewById(R.id.lv_exams);
        fabAddExams= (FloatingActionButton) findViewById(R.id.fabAddExam);

        addAdapter();

        lvExam.setOnItemClickListener(updateExamEventListener());
        lvExam.setOnItemLongClickListener(deleteExamEventListener());

        fabAddExams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ExamListActivity.this, AddExamActivity.class);
                startActivityForResult(intent, ADD_EXAM_REQUEST_CODE);
            }
        });

        examService = new ExamService(getApplicationContext());
        examService.getAll(getAllCallback());
    }

    private Callback<List<Exam>> getAllCallback() {
        return new Callback<List<Exam>>() {
            @Override
            public void runResultOnUiThread(
                    List<Exam> result) {
                if (result != null) {
                    listaExamene.clear();
                    listaExamene.addAll(result);
                    notifyAdapter();
                }
            }
        };
    }

    private Callback<Exam> insertCallback() {
        return new Callback<Exam>() {
            @Override
            public void runResultOnUiThread(Exam exam) {
                if (exam != null) {
                    listaExamene.add(exam);
                    notifyAdapter();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.failedMessage,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Callback<Exam> updateCallback() {
        return new Callback<Exam>() {
            @Override
            public void runResultOnUiThread(Exam exam) {
                if (exam != null) {
                    for (Exam e : listaExamene) {
                        if (e.getId() == exam.getId()) {
                            e.setNumarCredite(exam.getNumarCredite());
                            e.setTipExam(exam.getTipExam());
                            e.setDataSustinere(exam.getDataSustinere());
                            e.setOra(exam.getOra());
                            e.setDurataOre(exam.getDurataOre());
                            break;
                        }
                    }
                    notifyAdapter();
                }
            }
        };
    }

    private Callback<Integer> deleteCallback(final int position) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {
                    listaExamene.remove(position);
                    notifyAdapter();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXAM_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            Exam exam = (Exam) data.getSerializableExtra(AddExamActivity.ADD_EXAM);
            examService.insert(exam, insertCallback());

        } else if (requestCode == UPDATE_EXAM_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            Exam exam = (Exam) data
                    .getSerializableExtra(AddExamActivity.ADD_EXAM);
            examService.update(exam, updateCallback());
        }
    }

    private AdapterView.OnItemLongClickListener deleteExamEventListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                                           View view,
                                           int position,
                                           long id) {
                examService.delete(listaExamene.get(position),
                        deleteCallback(position));
                return true;
            }
        };
    }

    //Functie de update listview
    private AdapterView.OnItemClickListener updateExamEventListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(),
                        AddExamActivity.class);
                intent.putExtra(AddExamActivity.ADD_EXAM,
                        listaExamene.get(position));
                startActivityForResult(intent, UPDATE_EXAM_REQUEST_CODE);

            }
        };
    }

    private View.OnClickListener addExamEventListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ExamListActivity.this, AddExamActivity.class);
                startActivityForResult(intent, ADD_EXAM_REQUEST_CODE);
            }
        };
    }

    private void addAdapter() {
        ExamAdapter adapter = new ExamAdapter(getApplicationContext(), R.layout.exams_listview,
                listaExamene, getLayoutInflater());
        lvExam.setAdapter(adapter);
    }

    private void notifyAdapter() {
        ExamAdapter adapter = (ExamAdapter) lvExam.getAdapter();
        adapter.notifyDataSetChanged();
    }

}