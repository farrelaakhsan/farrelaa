package com.mycompany.farrelaa.dao;

import com.mycompany.farrelaa.User;

/** DAO (Data Access Object) untuk akun pengguna (login). */
public interface UserDao {

    // Kembalikan user jika username dan password cocok, atau null jika tidak ada
    User login(String username, String password);
}
