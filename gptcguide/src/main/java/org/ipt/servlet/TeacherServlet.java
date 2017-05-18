/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ipt.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.iptgptc.db.HikariPool;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.ipt.poly.DataFetch;
import org.ipt.poly.PasswordAuthentication;
/**
 *
 * @author musthafa
 */
@WebServlet(name = "Teacher", urlPatterns = {"/Teacher"})
public class TeacherServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try{
            
            
            String branchCode = request.getParameter("branch");
            String empNo = request.getParameter("empno");
            String email = request.getParameter("teacherEmail");
            String phone = request.getParameter("phone");
            String name = request.getParameter("teachername");
            String password = request.getParameter("password");
            branchCode = ESAPI.validator().getValidInput("Department Code",branchCode, "Dept", 3, false);
            empNo = ESAPI.validator().getValidInput("EmployeeNumber", empNo, "Number", 15, false);
            phone = ESAPI.validator().getValidInput("Phone Number", phone, "Phone", 14, false);
            email = ESAPI.validator().getValidInput("Email", email, "Email", 59, false);
            name = ESAPI.validator().getValidInput("Name", name, "Name", 30, false);
            password = ESAPI.validator().getValidInput("Password", password, "SafeString", 16, false);
            
            HikariPool pool = HikariPool.getInstance();
            DataFetch df = new DataFetch();
            log("on55");
            try {
                Connection con = pool.getConnection();
                PreparedStatement pstmnt = con.prepareStatement("INSERT INTO DB_GPTC.TeachersTbl"
                        + " (Dep_Id,Tchr_Name,Employee_no,Email,Phone)"
                        + " VALUES(?,?,?,?,?)");
                pstmnt.setInt(1, df.getDepId(branchCode));
                pstmnt.setString(2, name);
                pstmnt.setString(3, empNo);
                pstmnt.setString(4,email);
                pstmnt.setString(5, phone);
                pstmnt.executeUpdate();
                pstmnt.close();
                pstmnt = con.prepareStatement("INSERT INTO DB_GPTC.LoginTbl values (last_insert_id(),?,?,?,?)");
                byte[] salt = PasswordAuthentication.getNextSalt();
                byte[] hashed = PasswordAuthentication.hash(password.toCharArray(), salt);
                pstmnt.setString(1,empNo);
                pstmnt.setBytes(2,hashed);
                pstmnt.setBytes(3, salt);
                pstmnt.setString(4, "TCR");
                pstmnt.executeUpdate();
                con.close();
                session.setAttribute("form", "success");
            } catch (SQLException ex) {
                Logger.getLogger(TeacherServlet.class.getName()).log(Level.SEVERE, null, ex);
                session.setAttribute("form", "used");
            }
            
        }
        catch(NullPointerException |ValidationException |IntrusionException ex){
            Logger.getLogger(TeacherServlet.class.getName()).log(Level.SEVERE, null, ex);
            session.setAttribute("form", "error");
        }
        finally
        {
            request.getRequestDispatcher("/teacheradd.jsp").forward(request, response);
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
