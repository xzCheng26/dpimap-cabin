package com.dpi.map.cabin.controller;

import com.google.protobuf.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Test1 {

    public static void main(String[] args) throws Exception {
        while (true) {
            test();
            break;
        }
    }

    private static void test() throws IOException, InterruptedException,
            FileNotFoundException, Descriptors.DescriptorValidationException,
            InvalidProtocolBufferException {

        System.out.println("------init msg------");
        byte[] byteArray = initMsg();


        System.out.println("------generate descriptor------");
        // 生成descriptor文件
//        String protocCMD = "protoc --descriptor_set_out=D://cinema1.description D://cinema1.proto --proto_path=D://";

        String dir = "E:\\My_DPI_Version\\515-2";
        String source = dir + "/protoc/";
        String cmd = "cmd /c " + source + "protoc.exe -I=" + source + " --descriptor_set_out="+ source +"addressbook.desc "+ source +"dpi_me_output.proto";

        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            System.out.println("protoc execute failed");
            return;
        }

        System.out.println("------customer msg------");
        Descriptors.Descriptor pbDescritpor = null;
        DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet
                .parseFrom(new FileInputStream(source + "addressbook.desc"));
        for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet.getFileList()) {
            Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(fdp, new Descriptors.FileDescriptor[] {});

            for (Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {
                String className = fdp.getOptions().getJavaPackage() + "."
                        + fdp.getOptions().getJavaOuterClassname() + "$"
                        + descriptor.getName();
                System.out.println(descriptor.getFullName() + " -> "+ className);

                // 获取消息中的字段
                List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
//                Descriptors.FieldDescriptor fieldDescriptor = MyMessage.getDescriptor().findFieldByName("my_field");
                for (Descriptors.FieldDescriptor field : fields) {
                    System.out.println("Field Name: " + field.getName());
                    System.out.println("Field Type: " + field.getType());


                    // 获取字段的描述符


                    // 获取字段的名称和编号
                    String fieldName = field.getName();
                    int fieldNumber = field.getNumber();

                    // 获取字段的类型和Java类型
                    Descriptors.FieldDescriptor.Type fieldType = field.getType();
                    Descriptors.FieldDescriptor.JavaType javaType = field.getJavaType();

                    // 判断字段是否是重复字段、可选字段或必需字段
                    boolean isRepeated = field.isRepeated();
                    boolean isOptional = field.isOptional();
                    boolean isRequired = field.isRequired();

                    // 获取字段的默认值
//                    Object defaultValue = field.getDefaultValue();

                    // 打印字段的具体值
                    System.out.println("Field Name: " + fieldName);
                    System.out.println("Field Number: " + fieldNumber);
                    System.out.println("Field Type: " + fieldType);
                    System.out.println("Java Type: " + javaType);
                    System.out.println("Is Repeated: " + isRepeated);
                    System.out.println("Is Optional: " + isOptional);
                    System.out.println("Is Required: " + isRequired);
//                    System.out.println("Default Value: " + defaultValue);

                }

//                if (descriptor.getName().equals("Ticket")) {
//                    System.out.println("Movie descriptor found");
//                    pbDescritpor = descriptor;
//                    break;
//                }
            }
        }

        if (pbDescritpor == null) {
            System.out.println("No matched descriptor");
            return;
        }
        DynamicMessage.Builder pbBuilder = DynamicMessage.newBuilder(pbDescritpor);
        Message pbMessage = pbBuilder.mergeFrom(byteArray).build();

        //DynamicMessage parseFrom = DynamicMessage.parseFrom(pbDescritpor, byteArray);

        System.out.println(pbMessage);
    }

    private static byte[] initMsg() {
//        Cinema1.Movie.Builder movieBuilder = Cinema1.Movie.newBuilder();
//        movieBuilder.setName("The Shining");
//        movieBuilder.setType(Cinema1.MovieType.ADULT);
//        movieBuilder.setReleaseTimeStamp(327859200);
//        Movie movie = movieBuilder.build();
//        // byte[] byteArray = movie.toByteArray();
//
//        Cinema1.Movie.Builder movieBuilder1 = Cinema1.Movie.newBuilder();
//        movieBuilder1.setName("The Shining1");
//        movieBuilder1.setType(Cinema1.MovieType.CHILDREN);
//        movieBuilder1.setReleaseTimeStamp(327859201);
//        Movie movie1 = movieBuilder1.build();
//
//        Cinema1.Customer.Builder customerBuilder = Cinema1.Customer.newBuilder();
//        customerBuilder.setName("echo");
//        customerBuilder.setGender(Cinema1.Gender.MAN);
//        customerBuilder.setBirthdayTimeStamp(1231232333);
//
//        Cinema1.Ticket.Builder ticketBuilder = Cinema1.Ticket.newBuilder();
//        ticketBuilder.setId(1);
//        ticketBuilder.addMovie(movie);
//        ticketBuilder.addMovie(movie1);
//        ticketBuilder.setCustomer(customerBuilder.build());
//        Ticket ticket = ticketBuilder.build();
//        System.out.println(ticket.toString());
//        byte[] byteArray = ticket.toByteArray();

        return new byte[0];
    }
}