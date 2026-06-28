local stored = redis.call('GET', KEYS[1])
if stored == false or stored ~= ARGV[1] then
  return 0
end
redis.call('DEL', KEYS[1])
redis.call('DEL', KEYS[2])
return 1
