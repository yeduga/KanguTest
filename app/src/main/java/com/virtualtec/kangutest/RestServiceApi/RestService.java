package com.virtualtec.kangutest.RestServiceApi;

import com.virtualtec.kangutest.Datas.DataAddress;
import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataOrders;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.Datas.DataResponseDelivery;
import com.virtualtec.kangutest.Datas.DataUser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Android on 20/02/18.
 */

public interface RestService {

    // Login
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataUser>> Access(@Field("option") String option, @Field("username") String username, @Field("password") String password);

    // Login
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataUser>> AccessFacebook(@Field("option") String option, @Field("email") String email, @Field("first_name") String first_name, @Field("password") String password);

    // SignUp
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataUser> SignUp(@Field("option") String option, @Field("first_name") String first_name, @Field("last_name") String last_name, @Field("username") String username, @Field("password") String password, @Field("address_1") String address_1, @Field("address_2") String address_2, @Field("city") String city, @Field("state") String state, @Field("postcode") String postcode, @Field("email") String email, @Field("country") String country, @Field("phone") String phone);

    // Listas
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataList>> Lists(@Field("option") String option, @Field("customer_id") String customer_id);

    // Productos x Categoria
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataProducts>> ProductsCategory(@Field("option") String option, @Field("id_categoria") String id_categoria);

    // Categorias
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataCategory>> Categories(@Field("option") String option, @Field("id_categoria") String id_categoria);

    // Productos x Lista
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataProducts>> ProductsList(@Field("option") String option, @Field("id_list") String id_list);

    // Buscador productos
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataProducts>> SearchProducts(@Field("option") String option, @Field("search") String search);

    // Update Producto x Lista
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> UpdateProductxList(@Field("option") String option, @Field("id_detail_list") String id_detail_list, @Field("qty") String qty);

    // Update Producto x Lista
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> DeleteProductxList(@Field("option") String option, @Field("id_detail_list") String id_detail_list);

    // Crear Lista con Productos
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> CreateListProducts(@Field("option") String option, @Field("customer_id") String customer_id, @Field("name_list") String name_list , @Field("address") String address, @Field("array_product") String array_product);

    // Editar nombre Lista
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> EditListName(@Field("option") String option, @Field("id_list") String id_list, @Field("name_list") String name_list, @Field("address") String address);

    // Delete Lista - Cancelar suscripcion
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> DeleteList(@Field("option") String option, @Field("id_list") String id_list);

    // Agregar producto a muchas Listas
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> AddProductLits(@Field("option") String option, @Field("id_product") String id_product, @Field("qty") String qty, @Field("array_id_list") String array_id_list);

    // Listar Direcciones
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataAddress>> AddressxUser(@Field("option") String option, @Field("id_user") String id_user);

    // Agregar Direccion
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> CreateAddressxUser(@Field("option") String option, @Field("id_user") String id_user, @Field("address") String address);

    // Editar Direccion
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> UpdateAddressxUser(@Field("option") String option, @Field("id_list_address") String id_list_address, @Field("address") String address);

    // Eliminar Direccion
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> DeleteAddress(@Field("option") String option, @Field("id_list_address") String id_list_address);

    // Agregar direccion de lista
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> AddAddressList(@Field("option") String option, @Field("id_list") String id_list, @Field("address") String address);

    // Asignar direccion a la lista
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> AssignAddressList(@Field("option") String option, @Field("id_list") String id_list, @Field("address") String address);

    // Datos cliente
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataUser> ViewCustomer(@Field("option") String option, @Field("customer_id") String customer_id);

    // Update cliente
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> UpdateCustomer(@Field("option") String option, @Field("customer_id") String customer_id,
                                      @Field("first_name") String first_name, @Field("last_name") String last_name,
                                      @Field("address_1") String address_1, @Field("address_2") String address_2,
                                      @Field("city") String city, @Field("state") String state,
                                      @Field("postcode") String postcode, @Field("country") String country,
                                      @Field("phone") String phone);

    // Procesar Orden
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> CreateOrder(@Field("option") String option, @Field("customer_id") String customer_id,
                                   @Field("first_name") String first_name, @Field("last_name") String last_name,
                                   @Field("address_1") String address_1, @Field("address_2") String address_2,
                                   @Field("city") String city, @Field("state") String state,
                                   @Field("postcode") String postcode, @Field("country") String country,
                                   @Field("phone") String phone, @Field("email") String email,
                                   @Field("data_product") String data_product, @Field("customer_note") String customer_note);

    // Datos cliente
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponseDelivery> NextDelivery(@Field("option") String option, @Field("id_user") String id_user);

    // Datos cliente
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataOrders>> ListOrders(@Field("option") String option, @Field("customer_id") String customer_id);

    // Datos cliente
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<DataResponse> AddDelivery(@Field("option") String option, @Field("id_list") String id_list, @Field("address") String address, @Field("reminder") String reminder, @Field("date_delivery") String date_delivery);

    // Calendario list
    @POST("/tiendas_api/kangu/index.php")
    @FormUrlEncoded
    Call<ArrayList<DataResponseDelivery>> ListDelivery(@Field("option") String option, @Field("id_user") String id_user);
}
