package com.example.racinggame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // Элементы интерфейса
    ImageView car1, car2, finishLine;
    Button btnStart, btnMove1, btnMove2;
    // TextView txtResult; // Закомментировали, так как удалили из XML

    // Игровые флаги
    boolean isStarted = false;      // Началась ли гонка
    boolean isFinished = false;     // Закончилась ли гонка
    float car1Position = 0;         // Позиция первой машины
    float car2Position = 0;         // Позиция второй машины
    float finishPosition = 0;       // Позиция финиша

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
        // txtResult = findViewById(R.id.txtResult);

        // Вычисляем позицию финиша (ширина экрана минус ширина машины)
        // Это нужно, чтобы знать, когда машина победила
        finishPosition = getResources().getDisplayMetrics().widthPixels - car1.getWidth() - 100;
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
        } else {
            // Если игра не идёт — запускаем
            isStarted = true;
            btnStart.setText("ПАУЗА");
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
}