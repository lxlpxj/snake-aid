package cn.snake.snakeaidbackend.mapper;

import cn.snake.snakeaidbackend.entity.AppUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {

    @Select("SELECT * FROM app_user WHERE phone = #{phone} LIMIT 1")
    AppUser selectByPhone(String phone);
}
