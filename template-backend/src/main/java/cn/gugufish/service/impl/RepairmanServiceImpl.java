package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Account;
import cn.gugufish.entity.dto.Repairman;
import cn.gugufish.entity.vo.request.RepairmanCreateVO;
import cn.gugufish.entity.vo.response.RepairmanVO;
import cn.gugufish.mapper.AccountMapper;
import cn.gugufish.mapper.RepairmanMapper;
import cn.gugufish.service.RepairmanService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepairmanServiceImpl extends ServiceImpl<RepairmanMapper, Repairman> implements RepairmanService {

    @Resource
    AccountMapper accountMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String createRepairman(RepairmanCreateVO vo) {
        if (accountMapper.exists(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Account>().eq("username", vo.getUsername()))) {
            return "用户名已存在";
        }
        if (accountMapper.exists(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Account>().eq("email", vo.getEmail()))) {
            return "邮箱已存在";
        }

        Account account = new Account(null, vo.getUsername(),
                passwordEncoder.encode(vo.getPassword()), vo.getEmail(), "repairman", new Date());
        
        if (accountMapper.insert(account) > 0) {
            Repairman repairman = new Repairman();
            repairman.setAccountId(account.getId());
            repairman.setName(vo.getName());
            repairman.setPhone(vo.getPhone());
            repairman.setEmail(vo.getEmail());
            repairman.setIntroduction(vo.getIntroduction());
            repairman.setSpecialization(vo.getSpecialization());
            repairman.setCreateTime(new Date());
            
            if (this.save(repairman)) {
                return null;
            } else {
                throw new RuntimeException("维修工信息保存失败");
            }
        } else {
            return "账户创建失败";
        }
    }

    @Override
    public IPage<RepairmanVO> getRepairmanList(int pageNum, int pageSize) {
        Page<Repairman> page = new Page<>(pageNum, pageSize);
        this.page(page);
        
        return page.convert(repairman -> {
            RepairmanVO vo = new RepairmanVO();
            BeanUtils.copyProperties(repairman, vo);
            Account account = accountMapper.selectById(repairman.getAccountId());
            if (account != null) {
                vo.setUsername(account.getUsername());
            }
            return vo;
        });
    }

    @Override
    @Transactional
    public String updateRepairman(RepairmanVO vo) {
        Repairman repairman = this.getById(vo.getId());
        if (repairman == null) return "维修工不存在";
        
        repairman.setName(vo.getName());
        repairman.setPhone(vo.getPhone());
        repairman.setIntroduction(vo.getIntroduction());
        repairman.setSpecialization(vo.getSpecialization());
        
        if (this.updateById(repairman)) {
            return null;
        } else {
            return "更新失败";
        }
    }

    @Override
    @Transactional
    public String deleteRepairman(int id) {
        Repairman repairman = this.getById(id);
        if (repairman == null) return "维修工不存在";
        
        if (this.removeById(id)) {
            accountMapper.deleteById(repairman.getAccountId());
            return null;
        } else {
            return "删除失败";
        }
    }
}
