package com.example.racinggame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // Элементы интерфейса
    ImageView car1, car2, finishLine;
    Button btnStart, btnMove1, btnMove2, btnMode;
    // TextView txtResult; // Закомментировали, так как удалили из XML

    // Игровые флаги
    boolean isStarted = false;      // Началась ли гонка
    boolean isFinished = false;     // Закончилась ли гонка
    float car1Position = 0;         // Позиция первой машины
    float car2Position = 0;         // Позиция второй машины
    float finishPosition = 0;       // Позиция финиша

    // Для автоматического движения второй машины
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable autoMoveRunnable;
    boolean isSinglePlayer = true;  // true = игра на одного, false = на двоих

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов по их ID
        car1 = findViewById(R.id.car1);
        car2 = findViewById(R.id.car2);
        btnStart = findViewById(R.id.btnStart);
        btnMove1 = findViewById(R.id.btnMove1);
        btnMove2 = findViewById(R.id.btnMove2);
        finishLine = findViewById(R.id.finishLine);
        btnMode = findViewById(R.id.btnMode);
        // txtResult = findViewById(R.id.txtResult);

        // Вычисляем позицию финиша (ширина экрана минус ширина машины)
        // Это нужно, чтобы знать, когда машина победила
        finishPosition = getResources().getDisplayMetrics().widthPixels - car1.getWidth() - 100;
    }

    // Метод переключения режима игры
    public void onClickMode(View view) {
        if (isSinglePlayer) {
            // Переключаем на 2 игрока
            isSinglePlayer = false;
            btnMode.setText("2 ИГРОКА");
            btnMode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA500"))); // Оранжевый
            btnMove2.setVisibility(View.VISIBLE);  // Показываем кнопку для 2-го игрока
        } else {
            // Переключаем на 1 игрока
            isSinglePlayer = true;
            btnMode.setText("1 ИГРОК");
            btnMode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00AA00"))); // Зелёный
            btnMove2.setVisibility(View.GONE);  // Скрываем кнопку для 2-го игрока
        }

        // Сбрасываем игру при смене режима
        if (isStarted) {
            resetGame();
        }
    }
        // Метод для кнопки СТАРТ / ПАУЗА / РЕСТАРТ
    public void onClickStart(View view) {
        if (isFinished) {
            // Если игра закончена — перезапускаем
            resetGame();
            return;
        }

        if (isStarted) {
            // Если игра идёт — ставим на паузу
            isStarted = false;
            btnStart.setText("СТАРТ");
            stopAutoMove();  // Останавливаем автоматическое движение
        } else {
            // Если игра не идёт — запускаем
            isStarted = true;
            btnStart.setText("ПАУЗА");


            if (isSinglePlayer) {
                startAutoMove();  // Запускаем автоматическое движение для игры на одного
            }
        }
    }

    // Метод сброса игры (рестарт)
    private void resetGame() {
        isStarted = false;
        isFinished = false;
        car1Position = 0;
        car2Position = 0;

        // Возвращаем машины на стартовую позицию
        car1.setTranslationX(0);
        car2.setTranslationX(0);

        btnStart.setText("СТАРТ");

        // Разблокируем кнопки движения
        btnMove1.setEnabled(true);
        btnMove2.setEnabled(true);

        // Останавливаем автоматическое движение
        stopAutoMove();
    }

    // Метод движения первой машины (Игрок 1)
    public void onClickMove1(View view) {
        if (!isStarted || isFinished) {
            return;
        }

        // Получаем реальную позицию финиша (начало финишной черты минус ширина машины)
        float finishX = finishLine.getX() - car1.getWidth();  // Переименовали в finishX

        // Проверяем, не доехала ли уже машина до финиша
        if (car1Position >= finishX) {
            return;
        }

        // Двигаем машину вперёд на 50 пикселей
        car1Position += 50;

        // НЕ ДАЁМ машине заехать за финишную черту
        if (car1Position > finishX) {
            car1Position = finishX;
        }

        // Применяем новую позицию
        car1.setTranslationX(car1Position);

        // Проверяем финиш
        checkFinish(1, finishX);
    }

    // Метод движения второй машины (Игрок 2)
    public void onClickMove2(View view) {
        if (!isStarted || isFinished) {
            return;
        }

        // Получаем реальную позицию финиша (начало финишной черты минус ширина машины)
        float finishX = finishLine.getX() - car2.getWidth();  // Переименовали в finishX

        // Проверяем, не доехала ли уже машина до финиша
        if (car2Position >= finishX) {
            return;
        }

        // Двигаем машину вперёд на 50 пикселей
        car2Position += 50;

        // НЕ ДАЁМ машине заехать за финишную черту
        if (car2Position > finishX) {
            car2Position = finishX;
        }

        // Применяем новую позицию
        car2.setTranslationX(car2Position);

        // Проверяем финиш
        checkFinish(2, finishX);
    }

    // Метод проверки финиша
    private void checkFinish(int playerNumber, float finishX) {  // Переименовали параметр
        // Проверяем первую машину
        if (playerNumber == 1 && car1Position >= finishX) {
            isFinished = true;
            // Останавливаем машину ТОЧНО на финишной черте
            car1Position = finishX;
            car1.setTranslationX(car1Position);

            Toast.makeText(this, "Игрок 1 победил!", Toast.LENGTH_LONG).show();
            btnMove1.setEnabled(false);
            btnMove2.setEnabled(false);
            btnStart.setText("РЕСТАРТ");
        }

        // Проверяем вторую машину
        if (playerNumber == 2 && car2Position >= finishX) {
            isFinished = true;
            // Останавливаем машину ТОЧНО на финишной черте
            car2Position = finishX;
            car2.setTranslationX(car2Position);

            Toast.makeText(this, "Игрок 2 победил!", Toast.LENGTH_LONG).show();
            btnMove1.setEnabled(false);
            btnMove2.setEnabled(false);
            btnStart.setText("РЕСТАРТ");
        }
    }

    // Метод для запуска автоматического движения второй машины
    private void startAutoMove() {
        autoMoveRunnable = new Runnable() {
            @Override
            public void run() {
                if (isStarted && !isFinished) {
                    // Двигаем вторую машину автоматически
                    onClickMove2(null);

                    // Запускаем этот же код снова через 200 миллисекунд
                    handler.postDelayed(this, 200);
                }
            }
        };

        // Запускаем первое выполнение
        handler.post(autoMoveRunnable);
    }

    // Метод для остановки автоматического движения
    private void stopAutoMove() {
        if (autoMoveRunnable != null) {
            handler.removeCallbacks(autoMoveRunnable);
        }
    }
}