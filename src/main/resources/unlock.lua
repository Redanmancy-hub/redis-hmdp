---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by 86135.
--- DateTime: 2023/8/4 20:23
---
-- 比较线程标识与锁中的标识是否一致
if redis.call('get',KEYS[1] == ARGV[1]) then
    -- 释放锁del key
    return redis.call('del',KEYS[1])
end
return 0