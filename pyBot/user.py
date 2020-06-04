class User:
	def __init__(self, id_num, strikes = 0, spanks = 0):
		self.id_num = id_num
		self.strikes = strikes
		self.spanks = spanks

	
	def give_spank(self):
		self.spanks += 1
		return self.spanks


	def give_strike(self):
		self.strikes += 1
		return self.strikes


	def get_name(self):
		return self.name

	def get_id(self):
		return self.id_num