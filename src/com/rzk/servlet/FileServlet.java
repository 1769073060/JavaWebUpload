package com.rzk.servlet;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;


public class FileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进来了");
        //判断上传的文件是普通表单还是带文件的表单
        if (!ServletFileUpload.isMultipartContent(request)) {
            return;//终止方法运行，说明这是一个普通表单，直接返回
        }

        //创建上传文件的保存路径，建议在WEB-INF路径下，安全，用户无法直接访问上传的文件
        String uploadPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()) {
            uploadFile.mkdir();//创建这个目录
        }

        //缓存:临时文件
        //临时路径：加入文件超过了预期的大小，我们就把他放到一个临时文件，过几天，或者提醒用户转存为永久
        String tmpPath = this.getServletContext().getRealPath("/WEB-INF/tmp");
        File file = new File(tmpPath);
        if (!file.exists()) {//如果临时文件不存在
            file.mkdir();//创建这个目录
        }

        // 处理上传的文件，一般都需要通过流来获取，我们可以使用request.getInputStream(),原生态的文件上传流获取，十分麻烦
        //但是我们都建议使用 Apache的文件上传组件来实现  common-fileupload ,他需要依赖于commons-io 组件


        /*
         *   ServletFileUpload 负责处理上传的文件数据，并将表单中每个输入项封装成一个Fileitem对象
         *   在使用servletFileUpload 对象解析请求时需要 DiskFileItemFactory对象
         *   所以，我们需要在进行解析工作前构造好 DiskFileItemFactory 对象
         *   通过ServletFileUpload对象的构造方法  或 setFileItemFoctory()方法
         * 设置ServletFileUpload对象的fileItemFactory属性
         *
         * */

        DiskFileItemFactory factory = new DiskFileItemFactory();
        //通过这个工厂设置一个缓冲区，当上传的文件大于这个缓冲区的时候，将他放进临时文件
        factory.setSizeThreshold(1024 * 1024);//设置缓冲区大小为1M
        factory.setRepository(file);//临时目录的保存目录，需要一个File


        //2.获取ServletFileUpload
        ServletFileUpload upload = new ServletFileUpload(factory);
        //监听文件上传进度
        upload.setProgressListener(new ProgressListener() {
            //pBytesRead:已经读取到的文件大小
            //pContentLength : 文件大小
            public void update(long pBytesRead, long pContentLength, int pItems) {
                System.out.println("总大小" + pContentLength + "已上传" + pBytesRead);
            }
        });
        //处理乱码问题
        upload.setHeaderEncoding("UTF-8");
        //设置单个文件的最大值
        upload.setFileSizeMax(1024 * 1024 * 10);
        //设置总共能够上传文件的大小
        //1024 = 1kb * 1024 = 1M * 10 = 10M
        upload.setSizeMax(1024 * 1024 * 10);


        String msg = "";
        //把前端请求解析，封装成FileItem对象，需要从ServletFileUpload对象中获取
        List<FileItem> fileItems = null;
        try {
            fileItems = upload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        //fileItem 每一个表单对象
        for (FileItem fileItem : fileItems) {//fileItem
            //判断上传的文件是普通表单还是带文件的表单
            if (fileItem.isFormField()) {
                //getFieldName指的是前端表单控件的name
                String name = fileItem.getFieldName();
                String value = fileItem.getString("utf-8");
                System.out.println(name + " : " + value);
            } else {//如果是文件
                // ---------------------------处理文件---------------------
                String uploadFileName = fileItem.getName();
                //可能存在文件名不合法
                if (uploadFileName.trim().equals("") || uploadFileName == null) {
                    //如果名字为空
                    continue;
                }
                //获得上传的文件名   /images/popj.png
                String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
                //获得文件后缀名
                String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);

                    /*
                        如果文件后缀名 fileExtNAME 不是我们锁需要的
                        就直接return  不处理告诉用户名类型不对
                     */
                //可以使用UUID（唯一识别的通用码  ）保证文件名唯一
                //UUID.randomUUID() 随机生一个唯一识别的通用码

                //网络传输中的东西 都需要序列化
                // pojo 实体类   如果想要在多个电脑上运行   传输--》需要把对象都系列化了
                //implements Serializable ： 标记接口


                String uuidPath = UUID.randomUUID().toString();
                // ---------------------------存放地址---------------------
                //存到哪?  路径uploadPath
                //文件真实存在的路径realPath
                String realPath = uploadPath + "\\" + uuidPath;
                System.out.println(realPath);
                //给每个文件创建一个对应的文件夹
                File realPathFile = new File(realPath);
                if (!realPathFile.exists()) {
                    realPathFile.mkdir();
                }

                // ---------------------------文件传输---------------------
                //获得文件上传的流
                InputStream inputStream = fileItem.getInputStream();

                //创建一个文件输出流
                //realPath = 真实的文件夹
                // 差一个文件 ； 加上输出文件的名字+ "/" + uuidFileName
//                FileOutputStream fos = new FileOutputStream(realPath + "\\" + fileName);
                FileOutputStream fos = new FileOutputStream(realPath+"\\"+fileName);
                //创建一个缓冲区
                byte[] buffer = new byte[1024 * 1024];

                //判断是否读取完毕
                int len = 0;
                //如果大于0说明还存在数据
                while ((len = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                //关闭流
                fos.close();
                inputStream.close();
                msg = "文件上传成功";
                request.setAttribute("msg",msg);
                fileItem.delete();//上传成功，清除临时文件
                System.out.println("关闭");
                request.getRequestDispatcher("info.jsp").forward(request,response);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}































































